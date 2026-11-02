package me.sanyogjadhav.orderservice.service;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.orderservice.client.ProductServiceClient;
import me.sanyogjadhav.orderservice.dto.*;
import me.sanyogjadhav.orderservice.model.*;
import me.sanyogjadhav.orderservice.repository.OrderRepository;
import me.sanyogjadhav.orderservice.security.AuthenticatedUser;
import me.sanyogjadhav.orderservice.security.UserRole;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderResponseDTO createOrder(
            OrderRequestDTO request,
            AuthenticatedUser user,
            String token
    ) {

        if (user.role() != UserRole.CUSTOMER) {
            throw new AccessDeniedException(
                    "Only customers can place orders"
            );
        }

        Order order = new Order();

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
                        .toString().substring(0, 8).toUpperCase()
        );

        order.setCustomerId(user.userId());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        Long sellerId = null;

        List<OrderItem> orderItems = new ArrayList<>();

        // Track successful reservations for compensation
        List<OrderItemRequestDTO> reservedItems = new ArrayList<>();

        try {

            for (OrderItemRequestDTO itemRequest : request.getItems()) {

                ProductResponseDTO product =
                        productServiceClient.getProduct(
                                itemRequest.getProductId(),
                                token
                        );

                if (product == null) {
                    throw new EntityNotFoundException(
                            "Product not found: " + itemRequest.getProductId()
                    );
                }

                if (product.getCreatedBy() == null ||
                        !"FARMER".equals(product.getCreatedByRole())) {

                    throw new IllegalArgumentException(
                            "Only farmer products are supported currently"
                    );
                }

                if (sellerId == null) {
                    sellerId = product.getCreatedBy();
                } else if (!sellerId.equals(product.getCreatedBy())) {
                    throw new IllegalArgumentException(
                            "All products must belong to the same farmer"
                    );
                }

                BigDecimal price = product.getPrice();

                if (price == null || price.signum() < 0) {
                    throw new IllegalArgumentException(
                            "Invalid product price"
                    );
                }

                // Atomically reserve stock in Product Service
                productServiceClient.reserveStock(
                        product.getId(),
                        itemRequest.getQuantity()
                );

                reservedItems.add(itemRequest);

                BigDecimal subtotal = price.multiply(
                        BigDecimal.valueOf(itemRequest.getQuantity())
                );

                OrderItem orderItem = new OrderItem();

                orderItem.setOrder(order);
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(price);
                orderItem.setSubtotal(subtotal);

                orderItems.add(orderItem);

                total = total.add(subtotal);
            }

            order.setSellerId(sellerId);
            order.setTotalAmount(total);
            order.setItems(orderItems);

            Order savedOrder = orderRepository.save(order);

            return mapToResponse(savedOrder);

        } catch (RuntimeException exception) {

            // Restore previously reserved stock
            for (OrderItemRequestDTO item : reservedItems) {

                try {
                    productServiceClient.releaseStock(
                            item.getProductId(),
                            item.getQuantity()
                    );

                } catch (RuntimeException releaseException) {
                    // Log this for manual reconciliation/retry.
                    System.err.println(
                            "CRITICAL: Stock release failed for product "
                                    + item.getProductId()
                    );
                }
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyOrders(Long customerId) {

        return orderRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getSellerOrders(Long sellerId) {

        return orderRepository
                .findBySellerIdOrderByCreatedAtDesc(sellerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(
            Long orderId,
            AuthenticatedUser user
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order not found")
                );

        checkOrderAccess(order, user);

        return mapToResponse(order);
    }

    @Transactional
    public OrderResponseDTO cancelOrder(
            Long orderId,
            AuthenticatedUser user
    ) {

        Order order = orderRepository
                .findByIdAndCustomerId(orderId, user.userId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Order not found")
                );

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending orders can be cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return mapToResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus,
            AuthenticatedUser user
    ) {

        if (user.role() != UserRole.FARMER) {
            throw new AccessDeniedException(
                    "Only the seller can update order status"
            );
        }

        Order order = orderRepository
                .findByIdAndSellerId(orderId, user.userId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Order not found")
                );

        boolean validTransition = switch (order.getStatus()) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED -> newStatus == OrderStatus.PROCESSING
                    || newStatus == OrderStatus.CANCELLED;

            case PROCESSING -> newStatus == OrderStatus.SHIPPED;

            case SHIPPED -> newStatus == OrderStatus.DELIVERED;

            default -> false;
        };

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid order status transition"
            );
        }

        order.setStatus(newStatus);

        return mapToResponse(orderRepository.save(order));
    }

    private void checkOrderAccess(
            Order order,
            AuthenticatedUser user
    ) {

        boolean isCustomer = user.role() == UserRole.CUSTOMER
                && order.getCustomerId().equals(user.userId());

        boolean isSeller = user.role() == UserRole.FARMER
                && order.getSellerId().equals(user.userId());

        if (!isCustomer && !isSeller) {
            throw new AccessDeniedException(
                    "You are not authorized to access this order"
            );
        }
    }

    private OrderResponseDTO mapToResponse(Order order) {

        OrderResponseDTO response = new OrderResponseDTO();

        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomerId());
        response.setSellerId(order.getSellerId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(item -> {
                    OrderItemResponseDTO dto =
                            new OrderItemResponseDTO();

                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setSubtotal(item.getSubtotal());

                    return dto;
                })
                .toList();

        response.setItems(items);

        return response;
    }
}
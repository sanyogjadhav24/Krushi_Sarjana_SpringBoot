package me.sanyogjadhav.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import me.sanyogjadhav.orderservice.dto.OrderRequestDTO;
import me.sanyogjadhav.orderservice.dto.OrderResponseDTO;
import me.sanyogjadhav.orderservice.model.OrderStatus;
import me.sanyogjadhav.orderservice.security.AuthenticatedUser;
import me.sanyogjadhav.orderservice.service.OrderService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer creates an order
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, user, token));
    }

    // Customer views their orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                orderService.getMyOrders(user.userId())
        );
    }

    // Farmer views orders for their products
    @GetMapping("/seller-orders")
    public ResponseEntity<List<OrderResponseDTO>> getSellerOrders(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                orderService.getSellerOrders(user.userId())
        );
    }

    // Customer or farmer views a specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId, user)
        );
    }

    // Customer cancels a pending order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, user)
        );
    }

    // Farmer updates order status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderId, status, user)
        );
    }
}
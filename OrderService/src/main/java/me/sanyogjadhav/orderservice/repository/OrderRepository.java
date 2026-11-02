package me.sanyogjadhav.orderservice.repository;

import me.sanyogjadhav.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);

    Optional<Order> findByIdAndSellerId(Long id, Long sellerId);

    boolean existsByOrderNumber(String orderNumber);
}
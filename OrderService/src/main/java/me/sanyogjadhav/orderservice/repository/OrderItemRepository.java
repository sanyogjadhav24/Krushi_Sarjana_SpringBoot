package me.sanyogjadhav.orderservice.repository;

import me.sanyogjadhav.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}
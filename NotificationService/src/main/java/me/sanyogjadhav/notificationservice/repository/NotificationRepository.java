package me.sanyogjadhav.notificationservice.repository;

import me.sanyogjadhav.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(
            Long userId
    );

    long countByUserIdAndReadFalse(Long userId);

    boolean existsByEventIdAndUserId(String eventId, Long userId);
}
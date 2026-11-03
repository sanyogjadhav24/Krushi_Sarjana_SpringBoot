package me.sanyogjadhav.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.notificationservice.model.Notification;
import me.sanyogjadhav.notificationservice.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                repository.findByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        userId
                )
        );
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                repository.countByUserIdAndReadFalse(userId)
        );
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {

        Notification notification = repository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found"
                        )
                );

        if (!notification.getUserId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }

        notification.setRead(true);
        repository.save(notification);

        return ResponseEntity.noContent().build();
    }
}
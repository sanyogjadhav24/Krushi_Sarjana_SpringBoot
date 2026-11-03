package me.sanyogjadhav.notificationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.notificationservice.dto.NotificationEvent;
import me.sanyogjadhav.notificationservice.model.Notification;
import me.sanyogjadhav.notificationservice.model.NotificationType;
import me.sanyogjadhav.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationProcessorService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public void process(NotificationEvent event) {

        JsonNode data = event.data();

        switch (event.eventType()) {

            case "USER_REGISTERED" ->
                    notifyOne(
                            event,
                            data,
                            "userId",
                            "email",
                            "Welcome to Krushi Sarjana!",
                            "Your account has been registered successfully.",
                            NotificationType.USER_REGISTERED,
                            null
                    );

            case "PRODUCT_CREATED" ->
                    notifyOne(
                            event,
                            data,
                            "userId",
                            "email",
                            "Product published",
                            "Your product " + text(data, "productName")
                                    + " has been published successfully.",
                            NotificationType.PRODUCT_CREATED,
                            longValue(data, "productId")
                    );

            case "ORDER_PLACED" -> {

                String orderNumber = text(data, "orderNumber");

                notifyOne(
                        event, data,
                        "customerId", "customerEmail",
                        "Order placed successfully",
                        "Your order " + orderNumber
                                + " has been placed successfully.",
                        NotificationType.ORDER_PLACED,
                        longValue(data, "orderId")
                );

                notifyOne(
                        event, data,
                        "sellerId", "sellerEmail",
                        "New order received",
                        "You have received a new order: " + orderNumber,
                        NotificationType.ORDER_PLACED,
                        longValue(data, "orderId")
                );
            }

            case "ORDER_CONFIRMED",
                 "ORDER_SHIPPED",
                 "ORDER_DELIVERED",
                 "ORDER_CANCELLED" -> processOrderStatus(event, data);

            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.eventType()
            );
        }
    }

    private void processOrderStatus(
            NotificationEvent event,
            JsonNode data
    ) {

        String orderNumber = text(data, "orderNumber");

        String status = event.eventType().replace("ORDER_", "");

        String message = switch (status) {
            case "CONFIRMED" ->
                    "Your order " + orderNumber + " has been confirmed.";
            case "SHIPPED" ->
                    "Your order " + orderNumber + " has been shipped.";
            case "DELIVERED" ->
                    "Your order " + orderNumber + " has been delivered.";
            case "CANCELLED" ->
                    "Your order " + orderNumber + " has been cancelled.";
            default -> "Your order status has been updated.";
        };

        notifyOne(
                event,
                data,
                "customerId",
                "customerEmail",
                "Order " + status.toLowerCase(),
                message,
                NotificationType.valueOf(event.eventType()),
                longValue(data, "orderId")
        );
    }

    private void notifyOne(
            NotificationEvent event,
            JsonNode data,
            String userIdField,
            String emailField,
            String title,
            String message,
            NotificationType type,
            Long referenceId
    ) {

        Long userId = longValue(data, userIdField);
        String email = text(data, emailField);

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Missing recipient ID: " + userIdField
            );
        }

        if (!notificationRepository.existsByEventIdAndUserId(
                event.eventId(), userId
        )) {

            Notification notification = Notification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .referenceId(referenceId)
                    .eventId(event.eventId())
                    .read(false)
                    .build();

            notificationRepository.save(notification);
        }

        // Email is sent separately from the database insertion.
        if (email != null && !email.isBlank()) {
            emailService.sendEmail(email, title, message);
        }
    }

    private String text(JsonNode data, String field) {
        JsonNode value = data.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private Long longValue(JsonNode data, String field) {
        JsonNode value = data.get(field);
        return value == null || value.isNull() ? null : value.asLong();
    }
}
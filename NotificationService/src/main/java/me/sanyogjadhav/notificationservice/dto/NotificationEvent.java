package me.sanyogjadhav.notificationservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record NotificationEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        Instant timestamp,
        String source,
        JsonNode data
) {
}
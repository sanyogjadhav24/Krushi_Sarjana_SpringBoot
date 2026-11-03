package me.sanyogjadhav.userservice.dto;

import java.time.Instant;
import java.util.Map;

public record NotificationEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        Instant timestamp,
        String source,
        Map<String, Object> data
) {}
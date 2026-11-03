package me.sanyogjadhav.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.notificationservice.dto.NotificationEvent;
import me.sanyogjadhav.notificationservice.service.NotificationProcessorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationProcessorService processor;

    @KafkaListener(topics = "user-events")
    public void consume(String message) throws Exception {

        NotificationEvent event =
                objectMapper.readValue(message, NotificationEvent.class);

        processor.process(event);
    }
}
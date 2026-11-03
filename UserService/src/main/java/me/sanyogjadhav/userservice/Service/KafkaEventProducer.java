package me.sanyogjadhav.userservice.Service;

import me.sanyogjadhav.userservice.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, NotificationEvent event) {

        kafkaTemplate.send(topic, event.eventId(), event)
                .whenComplete((result, exception) -> {

                    if (exception != null) {
                        System.err.println(
                                "Failed to publish Kafka event: "
                                        + exception.getMessage()
                        );
                    } else {
                        System.out.println(
                                "Kafka event published: "
                                        + event.eventType()
                        );
                    }
                });
    }
}
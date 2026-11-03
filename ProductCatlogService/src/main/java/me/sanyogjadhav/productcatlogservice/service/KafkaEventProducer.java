
package me.sanyogjadhav.productcatlogservice.service;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.productcatlogservice.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

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

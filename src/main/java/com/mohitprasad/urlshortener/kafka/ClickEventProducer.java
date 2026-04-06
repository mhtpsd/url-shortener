package com.mohitprasad.urlshortener.kafka;

import com.mohitprasad.urlshortener.model.dto.ClickEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickEventProducer {

    private final KafkaTemplate<String, ClickEventMessage> kafkaTemplate;

    @Value("${kafka.topic.click-events:click-events}")
    private String clickEventsTopic;

    public void sendClickEvent(ClickEventMessage message) {
        CompletableFuture<SendResult<String, ClickEventMessage>> future =
                kafkaTemplate.send(clickEventsTopic, message.getShortCode(), message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send click event for shortCode={}: {}", message.getShortCode(), ex.getMessage());
            } else {
                log.debug("Click event sent for shortCode={}, offset={}",
                        message.getShortCode(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}

package com.mohitprasad.urlshortener.kafka;

import com.mohitprasad.urlshortener.model.dto.ClickEventMessage;
import com.mohitprasad.urlshortener.model.entity.ClickEvent;
import com.mohitprasad.urlshortener.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickEventConsumer {

    private final ClickEventRepository clickEventRepository;

    @KafkaListener(
            topics = "${kafka.topic.click-events:click-events}",
            groupId = "url-shortener-analytics",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeClickEvent(ClickEventMessage message) {
        try {
            log.debug("Received click event for shortCode={}", message.getShortCode());

            ClickEvent event = ClickEvent.builder()
                    .shortCode(message.getShortCode())
                    .clickedAt(message.getTimestamp())
                    .ipAddress(message.getIpAddress())
                    .userAgent(message.getUserAgent())
                    .referer(message.getReferer())
                    .build();

            clickEventRepository.save(event);
            log.debug("Saved click event for shortCode={}", message.getShortCode());
        } catch (Exception e) {
            log.error("Error processing click event for shortCode={}: {}", message.getShortCode(), e.getMessage(), e);
        }
    }
}

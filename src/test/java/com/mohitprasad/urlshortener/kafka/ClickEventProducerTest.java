package com.mohitprasad.urlshortener.kafka;

import com.mohitprasad.urlshortener.model.dto.ClickEventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClickEventProducerTest {

    @Mock
    private KafkaTemplate<String, ClickEventMessage> kafkaTemplate;

    @InjectMocks
    private ClickEventProducer clickEventProducer;

    @Test
    void sendClickEvent_successfullySendsMessage() {
        ReflectionTestUtils.setField(clickEventProducer, "clickEventsTopic", "click-events");

        ClickEventMessage message = ClickEventMessage.builder()
                .shortCode("abc123")
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla/5.0")
                .timestamp(OffsetDateTime.now())
                .build();

        CompletableFuture<SendResult<String, ClickEventMessage>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq("click-events"), eq("abc123"), eq(message))).thenReturn(future);

        clickEventProducer.sendClickEvent(message);

        verify(kafkaTemplate).send(eq("click-events"), eq("abc123"), eq(message));
    }
}

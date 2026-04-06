package com.mohitprasad.urlshortener.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.click-events:click-events}")
    private String clickEventsTopic;

    @Bean
    public NewTopic clickEventsTopic() {
        return TopicBuilder.name(clickEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

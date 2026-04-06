package com.mohitprasad.urlshortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Value("${rate-limit.window-seconds:60}")
    private int windowSeconds;

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }
}

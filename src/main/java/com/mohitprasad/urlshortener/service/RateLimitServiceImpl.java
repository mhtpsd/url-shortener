package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.config.RateLimitConfig;
import com.mohitprasad.urlshortener.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitConfig rateLimitConfig;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Override
    public void checkRateLimit(String ip) {
        String key = RATE_LIMIT_KEY_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(rateLimitConfig.getWindowSeconds()));
        }

        if (count != null && count > rateLimitConfig.getRequestsPerMinute()) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            throw new RateLimitExceededException("Rate limit exceeded. Maximum " +
                    rateLimitConfig.getRequestsPerMinute() + " requests per minute allowed.");
        }
    }
}

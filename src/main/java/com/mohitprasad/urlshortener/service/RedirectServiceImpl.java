package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.exception.UrlExpiredException;
import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.kafka.ClickEventProducer;
import com.mohitprasad.urlshortener.model.dto.ClickEventMessage;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.repository.ShortenedUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectServiceImpl implements RedirectService {

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final ClickEventProducer clickEventProducer;

    @Override
    @Transactional
    public ShortenedUrl resolveUrl(String shortCode, String ipAddress, String userAgent, String referer) {
        ShortenedUrl url = shortenedUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        if (url.getStatus() == UrlStatus.DISABLED) {
            throw new UrlNotFoundException("Short URL has been disabled: " + shortCode);
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new UrlExpiredException("Short URL has expired: " + shortCode);
        }

        shortenedUrlRepository.incrementClickCount(shortCode);

        publishClickEventAsync(shortCode, ipAddress, userAgent, referer);

        return url;
    }

    @Async
    protected void publishClickEventAsync(String shortCode, String ipAddress, String userAgent, String referer) {
        try {
            ClickEventMessage message = ClickEventMessage.builder()
                    .shortCode(shortCode)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .referer(referer)
                    .timestamp(OffsetDateTime.now())
                    .build();
            clickEventProducer.sendClickEvent(message);
        } catch (Exception e) {
            log.error("Failed to publish click event for shortCode={}: {}", shortCode, e.getMessage());
        }
    }
}

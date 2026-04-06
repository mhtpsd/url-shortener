package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.exception.CustomAliasAlreadyExistsException;
import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlResponse;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.repository.ShortenedUrlRepository;
import com.mohitprasad.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Transactional
    public CreateUrlResponse createShortUrl(CreateUrlRequest request, String clientIp) {
        String shortCode;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            if (shortenedUrlRepository.existsByShortCode(request.getCustomAlias())) {
                throw new CustomAliasAlreadyExistsException(
                        "Custom alias '" + request.getCustomAlias() + "' is already taken");
            }
            shortCode = request.getCustomAlias();
            ShortenedUrl entity = ShortenedUrl.builder()
                    .shortCode(shortCode)
                    .originalUrl(request.getUrl())
                    .customAlias(true)
                    .expiresAt(request.getExpiresAt())
                    .createdByIp(clientIp)
                    .build();
            entity = shortenedUrlRepository.save(entity);
            log.info("Created short URL with custom alias: {}", shortCode);
            return toResponse(entity);
        }

        // Auto-generate short code using base62(id)
        ShortenedUrl entity = ShortenedUrl.builder()
                .shortCode("PLACEHOLDER")
                .originalUrl(request.getUrl())
                .customAlias(false)
                .expiresAt(request.getExpiresAt())
                .createdByIp(clientIp)
                .build();
        entity = shortenedUrlRepository.save(entity);

        shortCode = base62Encoder.encode(entity.getId());
        entity.setShortCode(shortCode);
        entity = shortenedUrlRepository.save(entity);

        log.info("Created short URL: {}", shortCode);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public BulkCreateUrlResponse createBulkShortUrls(BulkCreateUrlRequest request, String clientIp) {
        List<CreateUrlResponse> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (CreateUrlRequest urlRequest : request.getUrls()) {
            try {
                CreateUrlResponse response = createShortUrl(urlRequest, clientIp);
                results.add(response);
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to shorten URL {}: {}", urlRequest.getUrl(), e.getMessage());
                failureCount++;
            }
        }

        return BulkCreateUrlResponse.builder()
                .results(results)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    @Override
    @Cacheable(value = "urls", key = "#shortCode")
    public ShortenedUrl getUrlByShortCode(String shortCode) {
        return shortenedUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
    }

    @Override
    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public void disableUrl(String shortCode) {
        ShortenedUrl url = shortenedUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
        shortenedUrlRepository.updateStatus(url.getShortCode(), UrlStatus.DISABLED);
        log.info("Disabled short URL: {}", shortCode);
    }

    private CreateUrlResponse toResponse(ShortenedUrl entity) {
        return CreateUrlResponse.builder()
                .shortCode(entity.getShortCode())
                .shortUrl(baseUrl + "/" + entity.getShortCode())
                .originalUrl(entity.getOriginalUrl())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}

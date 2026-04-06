package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.model.dto.UrlStatsResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.repository.ClickEventRepository;
import com.mohitprasad.urlshortener.repository.ShortenedUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final ClickEventRepository clickEventRepository;

    @Override
    public UrlStatsResponse getStats(String shortCode) {
        ShortenedUrl url = shortenedUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        long totalClicks = clickEventRepository.countByShortCode(shortCode);
        Map<String, Long> clicksByDate = toMap(clickEventRepository.countByShortCodeGroupByDate(shortCode));
        Map<String, Long> clicksByCountry = toMap(clickEventRepository.countByShortCodeGroupByCountry(shortCode));
        Map<String, Long> clicksByDevice = toMap(clickEventRepository.countByShortCodeGroupByDevice(shortCode));
        Map<String, Long> clicksByBrowser = toMap(clickEventRepository.countByShortCodeGroupByBrowser(shortCode));

        return UrlStatsResponse.builder()
                .shortCode(shortCode)
                .originalUrl(url.getOriginalUrl())
                .totalClicks(totalClicks)
                .createdAt(url.getCreatedAt())
                .clicksByDate(clicksByDate)
                .clicksByCountry(clicksByCountry)
                .clicksByDevice(clicksByDevice)
                .clicksByBrowser(clicksByBrowser)
                .build();
    }

    @Override
    public UrlStatsResponse getSummary(String shortCode) {
        ShortenedUrl url = shortenedUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        return UrlStatsResponse.builder()
                .shortCode(shortCode)
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .build();
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = row[0] != null ? row[0].toString() : "unknown";
            Long count = ((Number) row[1]).longValue();
            map.put(key, count);
        }
        return map;
    }
}

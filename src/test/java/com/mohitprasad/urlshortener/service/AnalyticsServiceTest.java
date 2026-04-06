package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.model.dto.UrlStatsResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.repository.ClickEventRepository;
import com.mohitprasad.urlshortener.repository.ShortenedUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ShortenedUrlRepository shortenedUrlRepository;

    @Mock
    private ClickEventRepository clickEventRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void getStats_returnsAggregatedStats() {
        ShortenedUrl url = ShortenedUrl.builder()
                .shortCode("abc")
                .originalUrl("https://example.com")
                .clickCount(5L)
                .status(UrlStatus.ACTIVE)
                .build();

        when(shortenedUrlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(clickEventRepository.countByShortCode("abc")).thenReturn(5L);
        when(clickEventRepository.countByShortCodeGroupByDate("abc")).thenReturn(List.of());
        when(clickEventRepository.countByShortCodeGroupByCountry("abc")).thenReturn(List.of());
        when(clickEventRepository.countByShortCodeGroupByDevice("abc")).thenReturn(List.of());
        when(clickEventRepository.countByShortCodeGroupByBrowser("abc")).thenReturn(List.of());

        UrlStatsResponse stats = analyticsService.getStats("abc");

        assertThat(stats.getShortCode()).isEqualTo("abc");
        assertThat(stats.getTotalClicks()).isEqualTo(5L);
        assertThat(stats.getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void getStats_whenNotFound_throwsException() {
        when(shortenedUrlRepository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analyticsService.getStats("missing"))
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    void getSummary_returnsSummaryStats() {
        ShortenedUrl url = ShortenedUrl.builder()
                .shortCode("abc")
                .originalUrl("https://example.com")
                .clickCount(42L)
                .status(UrlStatus.ACTIVE)
                .build();

        when(shortenedUrlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));

        UrlStatsResponse summary = analyticsService.getSummary("abc");

        assertThat(summary.getTotalClicks()).isEqualTo(42L);
        assertThat(summary.getShortCode()).isEqualTo("abc");
    }
}

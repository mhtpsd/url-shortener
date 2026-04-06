package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.model.dto.UrlStatsResponse;

public interface AnalyticsService {

    UrlStatsResponse getStats(String shortCode);

    UrlStatsResponse getSummary(String shortCode);
}

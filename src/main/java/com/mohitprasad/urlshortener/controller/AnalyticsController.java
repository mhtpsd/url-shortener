package com.mohitprasad.urlshortener.controller;

import com.mohitprasad.urlshortener.model.dto.UrlStatsResponse;
import com.mohitprasad.urlshortener.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for URL click analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{shortCode}")
    @Operation(summary = "Get full analytics for a short URL")
    public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(analyticsService.getStats(shortCode));
    }

    @GetMapping("/{shortCode}/summary")
    @Operation(summary = "Get summary analytics for a short URL")
    public ResponseEntity<UrlStatsResponse> getSummary(@PathVariable String shortCode) {
        return ResponseEntity.ok(analyticsService.getSummary(shortCode));
    }
}

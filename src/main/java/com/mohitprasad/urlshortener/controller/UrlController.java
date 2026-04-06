package com.mohitprasad.urlshortener.controller;

import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlResponse;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.service.QrCodeService;
import com.mohitprasad.urlshortener.service.RateLimitService;
import com.mohitprasad.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Tag(name = "URL Management", description = "Endpoints for creating and managing shortened URLs")
public class UrlController {

    private final UrlService urlService;
    private final QrCodeService qrCodeService;
    private final RateLimitService rateLimitService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @PostMapping
    @Operation(summary = "Create a short URL")
    public ResponseEntity<CreateUrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        rateLimitService.checkRateLimit(clientIp);
        CreateUrlResponse response = urlService.createShortUrl(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple short URLs")
    public ResponseEntity<BulkCreateUrlResponse> createBulkShortUrls(
            @Valid @RequestBody BulkCreateUrlRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        rateLimitService.checkRateLimit(clientIp);
        BulkCreateUrlResponse response = urlService.createBulkShortUrls(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Get URL info by short code")
    public ResponseEntity<ShortenedUrl> getUrlInfo(@PathVariable String shortCode) {
        ShortenedUrl url = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/{shortCode}")
    @Operation(summary = "Disable a short URL")
    public ResponseEntity<Void> disableUrl(@PathVariable String shortCode) {
        urlService.disableUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortCode}/qr")
    @Operation(summary = "Get QR code for a short URL")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String shortCode) {
        String shortUrl = baseUrl + "/" + shortCode;
        byte[] qrCode = qrCodeService.generateQrCode(shortUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

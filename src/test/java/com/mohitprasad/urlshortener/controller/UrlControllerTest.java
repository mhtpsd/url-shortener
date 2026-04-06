package com.mohitprasad.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.service.QrCodeService;
import com.mohitprasad.urlshortener.service.RateLimitService;
import com.mohitprasad.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    @MockBean
    private QrCodeService qrCodeService;

    @MockBean
    private RateLimitService rateLimitService;

    @Test
    void createShortUrl_validRequest_returns201() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://example.com");

        CreateUrlResponse response = CreateUrlResponse.builder()
                .shortCode("abc123")
                .shortUrl("http://localhost:8080/abc123")
                .originalUrl("https://example.com")
                .createdAt(OffsetDateTime.now())
                .build();

        doNothing().when(rateLimitService).checkRateLimit(anyString());
        when(urlService.createShortUrl(any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    void createShortUrl_invalidUrl_returns400() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("not-a-valid-url");

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUrlInfo_returns200() throws Exception {
        ShortenedUrl url = ShortenedUrl.builder()
                .id(1L)
                .shortCode("abc123")
                .originalUrl("https://example.com")
                .status(UrlStatus.ACTIVE)
                .build();

        when(urlService.getUrlByShortCode("abc123")).thenReturn(url);

        mockMvc.perform(get("/api/v1/urls/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

    @Test
    void disableUrl_returns204() throws Exception {
        doNothing().when(urlService).disableUrl("abc123");

        mockMvc.perform(delete("/api/v1/urls/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getQrCode_returns200WithPngImage() throws Exception {
        when(qrCodeService.generateQrCode(anyString())).thenReturn(new byte[]{0x1, 0x2, 0x3});

        mockMvc.perform(get("/api/v1/urls/abc123/qr"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }
}

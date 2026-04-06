package com.mohitprasad.urlshortener.controller;

import com.mohitprasad.urlshortener.exception.UrlExpiredException;
import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.service.RedirectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectService redirectService;

    @Test
    void redirect_validCode_returns302WithLocation() throws Exception {
        ShortenedUrl url = ShortenedUrl.builder()
                .shortCode("abc123")
                .originalUrl("https://example.com")
                .status(UrlStatus.ACTIVE)
                .build();

        when(redirectService.resolveUrl(eq("abc123"), any(), any(), any())).thenReturn(url);

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirect_notFound_returns404() throws Exception {
        when(redirectService.resolveUrl(eq("notfound"), any(), any(), any()))
                .thenThrow(new UrlNotFoundException("Not found"));

        mockMvc.perform(get("/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_expired_returns410() throws Exception {
        when(redirectService.resolveUrl(eq("expired"), any(), any(), any()))
                .thenThrow(new UrlExpiredException("Expired"));

        mockMvc.perform(get("/expired"))
                .andExpect(status().isGone());
    }
}

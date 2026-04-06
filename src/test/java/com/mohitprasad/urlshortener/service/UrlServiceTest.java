package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.exception.CustomAliasAlreadyExistsException;
import com.mohitprasad.urlshortener.exception.UrlNotFoundException;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import com.mohitprasad.urlshortener.repository.ShortenedUrlRepository;
import com.mohitprasad.urlshortener.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private ShortenedUrlRepository shortenedUrlRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://short.ly");
    }

    @Test
    void createShortUrl_withoutCustomAlias_generatesCode() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://example.com");

        ShortenedUrl savedEntity = ShortenedUrl.builder()
                .id(1L)
                .shortCode("PLACEHOLDER")
                .originalUrl("https://example.com")
                .status(UrlStatus.ACTIVE)
                .build();

        ShortenedUrl updatedEntity = ShortenedUrl.builder()
                .id(1L)
                .shortCode("1")
                .originalUrl("https://example.com")
                .status(UrlStatus.ACTIVE)
                .build();

        when(shortenedUrlRepository.save(any())).thenReturn(savedEntity, updatedEntity);
        when(base62Encoder.encode(1L)).thenReturn("1");

        CreateUrlResponse response = urlService.createShortUrl(request, "127.0.0.1");

        assertThat(response.getShortCode()).isEqualTo("1");
        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(response.getShortUrl()).isEqualTo("http://short.ly/1");
        verify(shortenedUrlRepository, times(2)).save(any());
    }

    @Test
    void createShortUrl_withCustomAlias_usesAlias() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("my-link");

        ShortenedUrl savedEntity = ShortenedUrl.builder()
                .id(1L)
                .shortCode("my-link")
                .originalUrl("https://example.com")
                .customAlias(true)
                .status(UrlStatus.ACTIVE)
                .build();

        when(shortenedUrlRepository.existsByShortCode("my-link")).thenReturn(false);
        when(shortenedUrlRepository.save(any())).thenReturn(savedEntity);

        CreateUrlResponse response = urlService.createShortUrl(request, "127.0.0.1");

        assertThat(response.getShortCode()).isEqualTo("my-link");
        verify(shortenedUrlRepository, times(1)).save(any());
    }

    @Test
    void createShortUrl_withExistingCustomAlias_throwsException() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("existing");

        when(shortenedUrlRepository.existsByShortCode("existing")).thenReturn(true);

        assertThatThrownBy(() -> urlService.createShortUrl(request, "127.0.0.1"))
                .isInstanceOf(CustomAliasAlreadyExistsException.class)
                .hasMessageContaining("existing");
    }

    @Test
    void getUrlByShortCode_whenNotFound_throwsException() {
        when(shortenedUrlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getUrlByShortCode("notexist"))
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    void disableUrl_whenFound_updatesStatus() {
        ShortenedUrl url = ShortenedUrl.builder()
                .id(1L)
                .shortCode("abc123")
                .originalUrl("https://example.com")
                .status(UrlStatus.ACTIVE)
                .build();

        when(shortenedUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        urlService.disableUrl("abc123");

        verify(shortenedUrlRepository).updateStatus("abc123", UrlStatus.DISABLED);
    }
}

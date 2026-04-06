package com.mohitprasad.urlshortener.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlResponse {

    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}

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
public class ClickEventMessage {

    private String shortCode;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private OffsetDateTime timestamp;
}

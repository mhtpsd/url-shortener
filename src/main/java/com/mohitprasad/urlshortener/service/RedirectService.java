package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;

public interface RedirectService {

    ShortenedUrl resolveUrl(String shortCode, String ipAddress, String userAgent, String referer);
}

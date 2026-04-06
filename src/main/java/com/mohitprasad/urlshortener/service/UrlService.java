package com.mohitprasad.urlshortener.service;

import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.BulkCreateUrlResponse;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;

public interface UrlService {

    CreateUrlResponse createShortUrl(CreateUrlRequest request, String clientIp);

    BulkCreateUrlResponse createBulkShortUrls(BulkCreateUrlRequest request, String clientIp);

    ShortenedUrl getUrlByShortCode(String shortCode);

    void disableUrl(String shortCode);
}

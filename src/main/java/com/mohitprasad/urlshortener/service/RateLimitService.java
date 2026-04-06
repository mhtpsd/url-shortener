package com.mohitprasad.urlshortener.service;

public interface RateLimitService {

    void checkRateLimit(String ip);
}

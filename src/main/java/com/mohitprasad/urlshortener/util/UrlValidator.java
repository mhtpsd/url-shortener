package com.mohitprasad.urlshortener.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {

    private static final int MAX_URL_LENGTH = 2048;

    public boolean isValid(String url) {
        if (url == null || url.isBlank() || url.length() > MAX_URL_LENGTH) {
            return false;
        }
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null &&
                   (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) &&
                   uri.getHost() != null &&
                   !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }
}

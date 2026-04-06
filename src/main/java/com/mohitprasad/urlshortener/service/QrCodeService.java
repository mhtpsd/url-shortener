package com.mohitprasad.urlshortener.service;

public interface QrCodeService {

    byte[] generateQrCode(String content, int width, int height);

    byte[] generateQrCode(String content);
}

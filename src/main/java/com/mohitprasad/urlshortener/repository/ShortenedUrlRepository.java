package com.mohitprasad.urlshortener.repository;

import com.mohitprasad.urlshortener.model.entity.ShortenedUrl;
import com.mohitprasad.urlshortener.model.enums.UrlStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {

    Optional<ShortenedUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Modifying
    @Query("UPDATE ShortenedUrl u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :shortCode")
    void incrementClickCount(@Param("shortCode") String shortCode);

    @Modifying
    @Query("UPDATE ShortenedUrl u SET u.status = :status WHERE u.shortCode = :shortCode")
    void updateStatus(@Param("shortCode") String shortCode, @Param("status") UrlStatus status);
}

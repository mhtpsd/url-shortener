package com.mohitprasad.urlshortener.repository;

import com.mohitprasad.urlshortener.model.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    long countByShortCode(String shortCode);

    @Query("SELECT DATE(c.clickedAt), COUNT(c) FROM ClickEvent c WHERE c.shortCode = :shortCode GROUP BY DATE(c.clickedAt) ORDER BY DATE(c.clickedAt)")
    List<Object[]> countByShortCodeGroupByDate(@Param("shortCode") String shortCode);

    @Query("SELECT c.country, COUNT(c) FROM ClickEvent c WHERE c.shortCode = :shortCode GROUP BY c.country")
    List<Object[]> countByShortCodeGroupByCountry(@Param("shortCode") String shortCode);

    @Query("SELECT c.deviceType, COUNT(c) FROM ClickEvent c WHERE c.shortCode = :shortCode GROUP BY c.deviceType")
    List<Object[]> countByShortCodeGroupByDevice(@Param("shortCode") String shortCode);

    @Query("SELECT c.browser, COUNT(c) FROM ClickEvent c WHERE c.shortCode = :shortCode GROUP BY c.browser")
    List<Object[]> countByShortCodeGroupByBrowser(@Param("shortCode") String shortCode);

    List<ClickEvent> findByShortCodeAndClickedAtBetween(String shortCode, OffsetDateTime from, OffsetDateTime to);
}

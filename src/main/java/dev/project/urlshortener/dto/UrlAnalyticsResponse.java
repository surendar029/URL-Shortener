package dev.project.urlshortener.dto;

import java.time.LocalDateTime;

public record UrlAnalyticsResponse(
        String shortCode,
        String longUrl,
        long clickCount,
        LocalDateTime createdAt,
        LocalDateTime expiryDate) {
}

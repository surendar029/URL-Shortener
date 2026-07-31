package dev.project.urlshortener.dto;

public record ShortenResponse(
    String shortUrl,
    String longUrl
){}


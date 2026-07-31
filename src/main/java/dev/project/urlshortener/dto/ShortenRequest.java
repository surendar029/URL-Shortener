package dev.project.urlshortener.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequest(
        @NotBlank(message = "URL cannot be empty") 
        @URL(message = "Invalid URL format") 
        String longUrl,
        String customAlias
) {}

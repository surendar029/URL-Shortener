package dev.project.urlshortener.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.project.urlshortener.dto.ShortenRequest;
import dev.project.urlshortener.dto.ShortenResponse;
import dev.project.urlshortener.dto.UrlAnalyticsResponse;
import dev.project.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/hello")
    public String getMethodName() {
        return new String("Hello Boss!");
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {
        String shortCode = urlShortenerService.shortenUrl(request);

        String shortUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(shortCode)
                .toUriString();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortUrl, request.longUrl()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlShortenerService.resolveUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @GetMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.status(HttpStatus.OK).body(urlShortenerService.getAnalytics(shortCode));
    }

    @DeleteMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlShortenerService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

}

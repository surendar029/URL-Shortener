package dev.project.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import dev.project.urlshortener.dto.ShortenRequest;
import dev.project.urlshortener.dto.UrlAnalyticsResponse;
import dev.project.urlshortener.entity.UrlMapping;
import dev.project.urlshortener.exception.CustomAliasAlreadyExistsException;
import dev.project.urlshortener.exception.UrlExpiredException;
import dev.project.urlshortener.exception.UrlNotFoundException;
import dev.project.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;
    private static final Logger log = LoggerFactory.getLogger(UrlShortenerService.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static Random random = new Random();

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    public String shortenUrl(ShortenRequest request) {
        log.info(request.longUrl());

        String shortCode = determineShortCode(request.customAlias());
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(request.longUrl());
        urlMapping.setShortCode(shortCode);
        urlMapping.setExpiryDate(LocalDateTime.now().plusHours(6));
        log.info("Successfully created short code: {} for URL: {}", shortCode, request.longUrl());
        urlMappingRepository.save(urlMapping);

        return shortCode;
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public String resolveUrl(String shortCode) {
        log.debug("Resolving short code: {}", shortCode);

        UrlMapping urlMapping = urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found: {}", shortCode);
                    return new UrlNotFoundException("ShortURL not found");
                });

        if (LocalDateTime.now().isAfter(urlMapping.getExpiryDate())) {
            log.warn("Short code has expired: {}", shortCode);
            throw new UrlExpiredException("This short link has expired");
        }

        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
        return urlMapping.getLongUrl();
    }


    @CacheEvict(value="urls",key="#shortCode")
    public void deleteUrl(String shortCode){
        UrlMapping urlMapping=urlMappingRepository
        .findByShortCode(shortCode)
        .orElseThrow(()-> new UrlNotFoundException("ShortURL not found"));

        urlMappingRepository.delete(urlMapping);
        log.info("Successfully deleted short code: {}", shortCode);
    }

    public UrlAnalyticsResponse getAnalytics(String shortCode) {

        UrlMapping urlMapping = urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short code not found: {}", shortCode);
                    return new UrlNotFoundException("ShortURL not found");
                });

        return new UrlAnalyticsResponse(
                urlMapping.getShortCode(),
                urlMapping.getLongUrl(),
                urlMapping.getClickCount(),
                urlMapping.getCreatedAt(),
                urlMapping.getExpiryDate());
    }

    private String determineShortCode(String customAlias) {
        if (StringUtils.hasText(customAlias)) {
            String alias = customAlias.trim();
            if (urlMappingRepository.existsByShortCode(alias)) {
                throw new CustomAliasAlreadyExistsException("Custom alias '" + alias + "' is already taken");
            }
            return alias;
        }
        return generateUniqueRandomCode();
    }

    private String generateUniqueRandomCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlMappingRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder str = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            str.append(CHARACTERS.charAt(index));
        }
        return str.toString();
    }

}
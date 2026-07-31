package dev.project.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.project.urlshortener.entity.UrlMapping;
import java.util.Optional;


public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

}

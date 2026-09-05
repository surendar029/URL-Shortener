# URL Shortener Service

## Description

A production-ready, high-throughput URL shortening and redirection platform built with **Java 17** and **Spring Boot 3**. The system converts long target URLs into compact, shareable short links with support for custom vanity aliases and configurable TTL expirations. 
Engineered with performance and resilience at its core, it leverages **Redis caching** to ensure sub-millisecond redirection lookups, **Bucket4j** token-bucket interceptors to enforce rate limiting (`429 Too Many Requests`), and automated expiration handling (`410 Gone`). Security is managed via stateless **JWT authentication** and **BCrypt** hashing, establishing clear `@ManyToOne` user-link ownership.


## Key Features

- **Vanity & Generated Slugs**: Generate short links or provide custom aliases.
- **Low-Latency Redirection**: Redis cache-aside implementation for fast target retrieval.
- **Rate Limiting (429)**: Token-bucket throttling with Bucket4j to block abusive traffic spikes.
- **Link Expiration (410)**: Automated TTL validation that marks expired links as `410 Gone`.
- **Click Analytics**: Tracks access counts and creation metadata per mapping.
- **Security & Ownership**: Stateless JWT authentication with BCrypt hashing and user URL ownership.
- **API Documentation**: Interactive Swagger / OpenAPI 3 UI.


## Tech Stack

- **Framework**: Java 17, Spring Boot 3, Spring Security, Spring Data JPA.
- **Database & Cache**: PostgreSQL, MySQL, Redis
- **Libraries**: Bucket4j, JJWT, Lombok
- **DevOps & Tools**: Docker, Docker Compose, Maven

---
## Category 
- High-Performance Backend
---

## Quick Start

### 1. Start Infrastructure
```bash
docker-compose up -d
```
### 2. Run Application

Bash

```
./mvnw spring-boot:run

```

- **Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

## API Endpoints

### Auth

- `POST /api/auth/register` — Create a user account
- `POST /api/auth/login` — Authenticate and receive JWT

### URLs

- `POST /api/v1/urls/shorten` — Shorten a URL (supports custom alias & expiration)
- `GET /{shortCode}` — Redirect to target URL
- `GET /api/v1/urls/{shortCode}/analytics` — Get click analytics

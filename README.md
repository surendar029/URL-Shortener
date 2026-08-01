# URL Shortener Microservice

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red?style=flat-square&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=flat-square&logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=flat-square&logo=swagger)](https://swagger.io/)

A high-throughput, production-ready URL Shortener RESTful microservice engineered with Java 17, Spring Boot 3, and Spring Security 6. Designed with a cache-aside architecture using Redis for low-latency HTTP redirects, distributed rate limiting via Bucket4j, stateless JWT authentication, and containerized deployment with Docker Compose.

---

## Technical Highlights & Architecture

- **Low-Latency Redirection (Cache-Aside Pattern)**: Integrated Spring Cache with Redis (`@Cacheable` / `@CacheEvict`) on URL resolution paths to deliver sub-millisecond HTTP 302 redirects and bypass relational database bottlenecks.
- **Stateless Authentication & Authorization**: Implemented Spring Security 6 filter chain with custom `OncePerRequestFilter` (`JwtAuthenticationFilter`), BCrypt password encoding, and role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
- **Resilience & Rate Limiting**: Built custom Spring MVC interceptor (`RateLimitingInterceptor`) leveraging Bucket4j token bucket algorithm for IP-based rate limiting (10 req/min), protecting against DDoS and automated scraping with HTTP 429 responses and retry headers.
- **Relational Domain Modeling**: Established bidirectional JPA relationships (`@ManyToOne` on `UrlMapping` and `@OneToMany` on `UserEntity`) with cascade lifecycle management and foreign key integrity.
- **Lifecycle & Expiration Management**: Automatic 6-hour URL expiration window with explicit HTTP 410 (`Gone`) handling, custom alias collision resolution, and click analytics tracking.
- **Global Error Handling**: Standardized exception handling via `@RestControllerAdvice` converting domain-specific exceptions to RFC-7807 compliant error responses.

---

## Tech Stack & Tooling

| Component | Technology | Version | Engineering Rationale |
|---|---|---|---|
| Language | Java | 17 | LTS release utilizing sealed classes, records, and enhanced pattern matching |
| Framework | Spring Boot | 3.x | Core application framework and dependency injection container |
| Security | Spring Security / JJWT | 6.x / 0.12.5 | Stateless JWT authentication filter chain and BCrypt hashing |
| Data Store | PostgreSQL | 16 | ACID-compliant relational persistence for user and mapping metadata |
| Cache Engine | Redis | 7.x | In-memory key-value store for high-frequency short code resolution |
| Rate Limiter | Bucket4j | 8.10.1 | In-memory token-bucket algorithm for request throttling |
| Containerization | Docker / Docker Compose | Latest | Multi-container orchestration for microservice dependencies |
| API Specification | SpringDoc OpenAPI | 3.x | Automated Swagger UI interactive documentation generation |
| Metrics | Spring Boot Actuator | 3.x | Application readiness, liveness, and telemetry endpoints |

---

## System Architecture Flow

```
                                  +---------------------------------+
                                  |     Client Request (HTTP/S)     |
                                  +---------------------------------+
                                                   |
                                                   v
                                  +---------------------------------+
                                  |   JwtAuthenticationFilter       |
                                  |   (Spring Security Context)     |
                                  +---------------------------------+
                                                   |
                                                   v
                                  +---------------------------------+
                                  |   RateLimitingInterceptor       |
                                  |   (Bucket4j IP Throttling)      |
                                  +---------------------------------+
                                                   |
                                         +---------+---------+
                                         |                   |
                                (Read / Redirect)     (Write / Analytics)
                                         |                   |
                                         v                   v
                               +------------------+ +------------------+
                               |   Redis Cache    | |  PostgreSQL DB   |
                               | (Sub-ms Lookup)  | |  (JPA / Hibernate|
                               +------------------+ +------------------+
```

---

## API Reference

### Endpoints Specification

| Method | Endpoint | Authorization | Request Body | Success Response |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | `{ "username": "...", "email": "...", "password": "..." }` | `201 Created` |
| `POST` | `/api/v1/auth/login` | Public | `{ "username": "...", "password": "..." }` | `200 OK` (JWT String) |
| `POST` | `/api/v1/urls` | Bearer Token | `{ "longUrl": "...", "customAlias": "..." }` | `201 Created` |
| `GET` | `/api/v1/urls/{shortCode}` | Bearer Token | None | `200 OK` (Analytics JSON) |
| `DELETE` | `/api/v1/urls/{shortCode}` | Bearer Token | None | `204 No Content` |
| `GET` | `/{shortCode}` | Public | None | `302 Found` (Location Header) |
| `GET` | `/swagger-ui.html` | Public | None | `200 OK` (Swagger UI) |
| `GET` | `/actuator/health` | Public | None | `200 OK` (Health Status) |

### Standard HTTP Status Mapping

| HTTP Code | Description | Trigger Condition |
|---|---|---|
| `201 Created` | Created | Resource successfully created (User / Short URL) |
| `302 Found` | Found / Redirect | Valid short code resolved; `Location` header populated |
| `400 Bad Request` | Bad Request | DTO bean validation failure (`@Valid`) |
| `401 Unauthorized` | Unauthorized | Missing, malformed, or expired JWT Bearer token |
| `404 Not Found` | Not Found | Target short code or user principal does not exist |
| `409 Conflict` | Conflict | Username, email, or custom short code alias already taken |
| `410 Gone` | Gone | Short link expiry timestamp has elapsed (> 6 hours) |
| `429 Too Many Requests` | Rate Limited | Request rate exceeds token bucket capacity (10 req/min) |

---
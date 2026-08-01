# 🔗 URL Shortener — Production-Ready REST API

> A **high-performance, production-ready URL Shortener REST API** built with Java 17 and Spring Boot 3.  
> Implements stateless JWT authentication, Redis caching with sub-millisecond redirect latency, API rate limiting, click analytics, URL expiration, and multi-container Docker orchestration.

---

## 📌 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
- [Docker Deployment](#-docker-deployment)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **JWT Authentication** | Stateless auth using JJWT 0.12.5 with Bearer token & BCrypt password hashing |
| 👥 **User Registration & Login** | Secure user registration with role-based access control (`ROLE_USER`, `ROLE_ADMIN`) |
| 🔗 **URL Shortening** | Generates a unique 6-character alphanumeric short code for any long URL |
| ✏️ **Custom Alias** | Users can define a custom short code alias (e.g., `/my-brand`) |
| ⏱️ **URL Expiration** | Short links auto-expire after **6 hours**, returning `410 Gone` |
| 📊 **Click Analytics** | Tracks click count, creation timestamp, and expiry date per short link |
| 🚀 **Redis Caching** | `@Cacheable` on redirect path with `@CacheEvict` on deletion for sub-millisecond hits |
| 🛡️ **API Rate Limiting** | Bucket4j Token Bucket — **10 requests/minute** per client IP, HTTP `429` on breach |
| 🔗 **URL Ownership** | `@ManyToOne` / `@OneToMany` JPA relationship links each short URL to its creator |
| 🐳 **Docker Compose** | Multi-container orchestration for App + PostgreSQL + Redis |
| 📖 **Swagger UI** | Interactive OpenAPI 3 documentation at `/swagger-ui.html` |
| 🩺 **Actuator** | Health checks and metrics via Spring Boot Actuator |

---

## 🏗️ Architecture

```
 Client (Postman / Browser)
         │
         ▼
 ┌──────────────────────────────────────────────────────────────┐
 │                    Spring Boot Application                    │
 │                                                              │
 │  ┌──────────────┐   ┌──────────────┐   ┌────────────────┐  │
 │  │  JWT Auth    │   │ Rate Limit   │   │  Controllers   │  │
 │  │  Filter      │──▶│ Interceptor  │──▶│  /api/v1/**    │  │
 │  │(OncePerReq)  │   │  (Bucket4j)  │   │                │  │
 │  └──────────────┘   └──────────────┘   └───────┬────────┘  │
 │                                                 │           │
 │                                          ┌──────▼──────┐   │
 │                                          │   Services  │   │
 │                                          │  (Business  │   │
 │                                          │   Logic)    │   │
 │                                          └──────┬──────┘   │
 │                                                 │           │
 │                            ┌────────────────────┤           │
 │                            │                    │           │
 │                    ┌───────▼──────┐    ┌────────▼──────┐  │
 │                    │    Redis     │    │  PostgreSQL   │  │
 │                    │  (30m TTL)   │    │    (JPA)      │  │
 │                    └──────────────┘    └───────────────┘  │
 └──────────────────────────────────────────────────────────────┘
```

### Database Schema

```sql
-- users table
CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR UNIQUE NOT NULL,
    email    VARCHAR UNIQUE NOT NULL,
    password VARCHAR        NOT NULL,
    active   BOOLEAN        NOT NULL DEFAULT TRUE,
    role     VARCHAR        NOT NULL
);

-- url_mapping table
CREATE TABLE url_mapping (
    id          BIGSERIAL PRIMARY KEY,
    long_url    VARCHAR   NOT NULL,
    short_code  VARCHAR   UNIQUE NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP,
    click_count BIGINT    NOT NULL DEFAULT 0,
    user_id     BIGINT    REFERENCES users(id)  -- FK: URL Ownership
);
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security 6, JWT (JJWT 0.12.5), BCrypt |
| **Persistence** | Spring Data JPA, Hibernate, PostgreSQL 16 |
| **Caching** | Redis, Spring Cache (`@Cacheable`, `@CacheEvict`) |
| **Rate Limiting** | Bucket4j 8.10.1 (Token Bucket Algorithm) |
| **Build Tool** | Maven |
| **Containerization** | Docker, Docker Compose |
| **API Docs** | SpringDoc OpenAPI 3 / Swagger UI |
| **Monitoring** | Spring Boot Actuator |
| **Testing** | JUnit 5, Mockito |

---

## 📡 API Reference

### 🔓 Auth Endpoints (Public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register a new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT Bearer token |

### 🔐 URL Endpoints (Requires `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/urls` | Shorten a URL (with optional custom alias) |
| `GET` | `/api/v1/urls/{shortCode}` | Get click analytics for a short link |
| `DELETE` | `/api/v1/urls/{shortCode}` | Delete a short link and evict from cache |

### 🌐 Public Redirect

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/{shortCode}` | Redirect to original URL (`302 Found`) |

---

### 📋 Example Requests

**1. Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"haris","email":"haris@example.com","password":"secure123"}'
```

**2. Login (get JWT token):**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"haris","password":"secure123"}'
```

**3. Shorten a URL:**
```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{"longUrl":"https://github.com/surendar029","customAlias":"my-github"}'
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/my-github",
  "longUrl": "https://github.com/surendar029"
}
```

**4. Get Analytics:**
```bash
curl -X GET http://localhost:8080/api/v1/urls/my-github \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Response:**
```json
{
  "shortCode": "my-github",
  "longUrl": "https://github.com/surendar029",
  "clickCount": 42,
  "createdAt": "2026-08-01T10:00:00",
  "expiryDate": "2026-08-01T16:00:00"
}
```

---

### 📊 HTTP Status Code Reference

| Status | Meaning |
|---|---|
| `201 Created` | Short URL created successfully |
| `302 Found` | Redirect to original URL |
| `400 Bad Request` | Validation error on request body |
| `401 Unauthorized` | Missing or invalid JWT token |
| `404 Not Found` | Short code does not exist |
| `409 Conflict` | Custom alias or username already taken |
| `410 Gone` | Short link has expired (after 6 hours) |
| `429 Too Many Requests` | Rate limit exceeded (10 req/min per IP) |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+

### 1. Clone the Repository

```bash
git clone https://github.com/surendar029/URL-Shortener.git
cd URL-Shortener
```

### 2. Configure the Database

```sql
CREATE DATABASE url_shortener;
CREATE USER admin WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE url_shortener TO admin;
```

### 3. Set Environment Variables

```bash
# Windows PowerShell
$env:JWT_SECRET="your-256-bit-secret-key-here"
$env:JWT_EXPIRATION="86400000"
```

### 4. Run the Application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Open Swagger UI

Visit: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🐳 Docker Deployment

Run the complete stack (App + PostgreSQL + Redis) with one command:

```bash
docker-compose up --build
```

| Service | URL |
|---|---|
| **Spring Boot App** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **Actuator Health** | `http://localhost:8080/actuator/health` |
| **PostgreSQL** | `localhost:5432` |
| **Redis** | `localhost:6379` |

Stop all containers:
```bash
docker-compose down
```

---

## ⚙️ Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | Application port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/url_shortener` | PostgreSQL connection |
| `spring.data.redis.host` | `localhost` | Redis host |
| `spring.cache.redis.time-to-live` | `30m` | Redis cache TTL |
| `jwt.secret` | env `JWT_SECRET` | JWT signing key (256-bit) |
| `jwt.expiration` | `86400000` (24h) | JWT expiry in milliseconds |

---

## 📁 Project Structure

```
src/main/java/dev/project/urlshortener/
├── config/
│   ├── CacheConfig.java                # @EnableCaching configuration
│   ├── JwtAuthenticationFilter.java    # JWT Bearer token filter (OncePerRequestFilter)
│   ├── RateLimitingInterceptor.java    # Bucket4j rate limiter (10 req/min per IP)
│   ├── SecurityConfig.java             # Spring Security 6 filter chain & RBAC
│   └── WebConfig.java                  # MVC interceptor registration
├── controller/
│   ├── UrlShortenerController.java     # URL CRUD + redirect + analytics endpoints
│   └── UserController.java             # Auth register/login endpoints
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── ShortenRequest.java
│   ├── ShortenResponse.java
│   ├── UrlAnalyticsResponse.java
│   └── ErrorResponse.java
├── entity/
│   ├── Role.java                       # ROLE_USER / ROLE_ADMIN enum
│   ├── UrlMapping.java                 # @ManyToOne UserEntity (user_id FK)
│   └── UserEntity.java                 # @OneToMany List<UrlMapping>
├── exception/
│   ├── GlobalExceptionHandler.java     # @RestControllerAdvice centralized handler
│   ├── CustomAliasAlreadyExistsException.java
│   ├── UrlExpiredException.java
│   ├── UrlNotFoundException.java
│   ├── UserAlreadyExistsException.java
│   └── UserNotFoundException.java
├── repository/
│   ├── UrlMappingRepository.java
│   └── UserRepository.java
├── service/
│   ├── CustomUserDetailsService.java   # Spring Security UserDetailsService impl
│   ├── RateLimitingService.java        # IP-based bucket registry (ConcurrentHashMap)
│   ├── UrlShortenerService.java        # Core URL business logic + Redis cache ops
│   └── UserService.java                # User registration + JWT login
└── util/
    └── JwtUtil.java                    # JWT generation, parsing & validation (JJWT 0.12.5)
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 👤 Author

**Surendar** — *Java Backend Developer*

[![GitHub](https://img.shields.io/badge/GitHub-surendar029-181717?style=flat-square&logo=github)](https://github.com/surendar029)

> *Built as a hands-on production learning project demonstrating real-world Spring Boot backend patterns: security, caching, rate limiting, and containerization.*

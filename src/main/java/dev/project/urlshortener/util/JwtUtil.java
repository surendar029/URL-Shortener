package dev.project.urlshortener.util;

import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .addClaims(new HashMap<>())
                .signWith(getSignedKey())
                .compact();
    }

    public SecretKey getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Claims verifySignatureAndExtractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String extractUsername(String token){
        return verifySignatureAndExtractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        return verifySignatureAndExtractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token,String username){
        String extractedUsername = extractUsername(token);
        return (username.equals(extractedUsername) && !isTokenExpired(token)); 
    }
}

package com.cabbooking.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component; // Import JwtParser

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey jwtSecret = Keys.hmacShaKeyFor("your-256-bit-secret-your-256-bit-secret".getBytes());

    private final long jwtExpirationInMs = 24 * 60 * 60 * 1000;

    // Create a reusable, thread-safe parser instance
    private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtExpirationInMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role) // Add role as a claim
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        // Use the pre-built parser
        Jws<Claims> claims = jwtParser.parseSignedClaims(token);
        return claims.getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // Use the pre-built parser
            jwtParser.parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Add this new method
    public Jws<Claims> getClaimsFromJWT(String token) {
        return jwtParser.parseSignedClaims(token);
    }

    }
            
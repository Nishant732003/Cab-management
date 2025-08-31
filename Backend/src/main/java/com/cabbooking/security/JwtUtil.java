package com.cabbooking.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * A utility component for handling JSON Web Tokens (JWTs).
 *
 * Main Responsibilities:
 * - Generating new JWTs for authenticated users.
 * - Parsing and validating incoming JWTs.
 * - Extracting claims (like username and role) from tokens.
 *
 * Workflow:
 * - After a user successfully logs in, the `LoginService` calls `generateToken` to create a JWT.
 * - For every subsequent request to a protected endpoint, the `JwtAuthenticationFilter` uses this utility
 * to validate the token and extract user details.
 */
@Component
public class JwtUtil {

    // A secure, private key used to sign and verify all JWTs. 
    // IMPORTANT: In a real application, load this from your application.properties or environment variables.
    private final SecretKey jwtSecret = Keys.hmacShaKeyFor("your-256-bit-secret-key-for-jwt-signing-must-be-32-chars".getBytes());

    // The validity duration for a token, set to 24 hours in milliseconds.
    private final long jwtExpirationInMs = 24 * 60 * 60 * 1000;

    /**
     * Generates a new JWT for a given user.
     *
     * @param username The username of the user for whom the token is generated.
     * @param role The role of the user (e.g., "Admin", "Customer"), which is added as a custom claim.
     * @return A compact, URL-safe JWT string.
     */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Add the user's role as a custom claim
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(jwtSecret)
                .compact();
    }

    /**
     * Extracts the username (subject) from a given JWT.
     *
     * @param token The JWT string to parse.
     * @return The username contained within the token.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(jwtSecret)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

    /**
     * Validates a JWT to ensure it has not been tampered with and has not expired.
     *
     * @param token The JWT string to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // You can log the specific error here for debugging if you want
            return false;
        }
    }

    /**
     * Parses the JWT and returns the full set of claims.
     * This is used to extract custom data like the user's role.
     *
     * @param token The JWT string to parse.
     * @return A Jws object containing all the token's claims.
     */
    public Jws<Claims> getClaimsFromJWT(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(jwtSecret)
                   .build()
                   .parseClaimsJws(token);
    }
}

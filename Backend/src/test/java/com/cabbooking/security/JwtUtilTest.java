package com.cabbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil class.
 * Covers generation, extraction, validation, and claim retrieval of JWT tokens.
 */
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Initialize JwtUtil before each test
        jwtUtil = new JwtUtil();
    }

    /**
     * Test: generateToken
     * Scenario: Generate a JWT for a valid username and role
     * Expected: Token is non-null and has non-zero length
     */
    @Test
    void generateToken_createsValidToken() {
        String username = "testuser";
        String role = "CUSTOMER";

        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    /**
     * Test: getUsernameFromJWT
     * Scenario: Extract username from a valid token
     * Expected: Extracted username matches the original
     */
    @Test
    void getUsernameFromJWT_extractsCorrectUsername() {
        String username = "testuser";
        String role = "CUSTOMER";
        String token = jwtUtil.generateToken(username, role);

        String extractedUsername = jwtUtil.getUsernameFromJWT(token);

        assertEquals(username, extractedUsername);
    }

    /**
     * Test: validateToken
     * Scenario: Validate a valid token
     * Expected: Returns true
     */
    @Test
    void validateToken_validToken_returnsTrue() {
        String username = "testuser";
        String role = "CUSTOMER";
        String token = jwtUtil.generateToken(username, role);

        assertTrue(jwtUtil.validateToken(token));
    }

    /**
     * Test: validateToken
     * Scenario: Validate an invalid/malformed token
     * Expected: Returns false
     */
    @Test
    void validateToken_invalidToken_returnsFalse() {
        String invalidToken = "invalid.token.string";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    /**
     * Test: validateToken (conceptual)
     * Scenario: Validate an expired token
     * Note: Current JwtUtil uses a fixed expiration; test requires configurable expiration.
     * Expected: Returns false for expired token
     */
    @Test
    void validateToken_expiredToken_returnsFalse() throws InterruptedException {
        // This test is a placeholder; implementing requires configurable expiration.
        // Proper approach: JwtUtil should allow passing expiration time for testability.
    }

    /**
     * Test: getClaimsFromJWT
     * Scenario: Extract claims from a valid token
     * Expected: Claims object is not null and contains correct subject (username) and role
     */
    @Test
    void getClaimsFromJWT_extractsClaims() {
        String username = "testuser";
        String role = "CUSTOMER";
        String token = jwtUtil.generateToken(username, role);

        Jws<Claims> claims = jwtUtil.getClaimsFromJWT(token);

        assertNotNull(claims);
        assertEquals(username, claims.getBody().getSubject());
        assertEquals(role, claims.getBody().get("role"));
    }
}

package com.cabbooking.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cabbooking.repository.BlacklistedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A custom Spring Security filter that intercepts every incoming HTTP request once.
 *
 * Main Responsibilities:
 * - Extracts the JWT from the 'Authorization' header.
 * - Checks if the token has been blacklisted (i.e., the user has logged out).
 * - Validates the token's signature and expiration.
 * - Parses the user's username and role (authority) from the token.
 * - Sets the user's authentication details in the Spring Security context for the duration of the request.
 *
 * Workflow:
 * - This filter is executed before the standard Spring Security authentication filters.
 * - If a valid, non-blacklisted token is found, it authenticates the user, allowing them to access protected endpoints.
 * - If no token is found, it passes the request down the filter chain, where access will be denied for protected endpoints.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * The core logic of the filter that is executed for each request.
     *
     * @param request The incoming HttpServletRequest.
     * @param response The outgoing HttpServletResponse.
     * @param filterChain The chain of subsequent filters.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String username = null;
        String token = null;

        // Extract the token from the "Bearer " header
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            // Check if the token has been blacklisted (user logged out)
            var isBlacklisted = blacklistedTokenRepository.findByToken(token).isPresent();
            if (isBlacklisted) {
                // Reject the request if the token is invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            // Validate the token and extract the username
            try {
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.getUsernameFromJWT(token);
                }
            } catch (Exception e) {
                // Can be expanded to log token validation errors
            }
        }

        // If a valid username is extracted and the user is not already authenticated in this session
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Parse the token to extract the user's role from the claims
            Jws<Claims> claimsJws = jwtUtil.getClaimsFromJWT(token);
            Claims claims = claimsJws.getBody();
            String role = claims.get("role", String.class);

            // Create a Spring Security authority with the required "ROLE_" prefix
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            // Create an authentication object representing the authenticated user
            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(
                            username, null,
                            authorities
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the request processing down the filter chain
        filterChain.doFilter(request, response);
    }
}

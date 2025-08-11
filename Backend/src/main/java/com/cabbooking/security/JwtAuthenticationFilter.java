package com.cabbooking.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cabbooking.repository.BlacklistedTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that checks incoming requests for JWT token and authenticates user.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository; // Inject the repository

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String username = null;
        String token;

        // JWT token is expected as: "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            // ==> CHECK IF TOKEN IS BLACKLISTED <==
            var isBlacklisted = blacklistedTokenRepository.findByToken(token).isPresent();
            if (isBlacklisted) {
                // If token is blacklisted, send an unauthorized error and stop the chain
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            try {
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.getUsernameFromJWT(token);
                }
            } catch (Exception e) {
                // Log or handle token exception if desired
            }
        }

        // If username extracted from token and SecurityContext not yet authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Create a simple GrantedAuthority - here a default ROLE_USER assigned.
            // You can adapt your JWT token to include user roles if you wish.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username, null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}

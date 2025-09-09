package com.cabbooking.security;

import com.cabbooking.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;
    
    @Mock
    private Jws<Claims> claimsJws;

    @Mock
    private Claims claims;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void doFilterInternal_validToken_setsAuthenticationInContext() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String header = "Bearer " + token;
        String username = "testuser";
        String role = "CUSTOMER";

        when(request.getHeader("Authorization")).thenReturn(header);
        when(blacklistedTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromJWT(token)).thenReturn(username);
        when(jwtUtil.getClaimsFromJWT(token)).thenReturn(claimsJws);
        when(claimsJws.getBody()).thenReturn(claims);
        when(claims.get("role", String.class)).thenReturn(role);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(role.toUpperCase())));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String header = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(header);
        when(blacklistedTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        when(jwtUtil.validateToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void doFilterInternal_blacklistedToken_returnsUnauthorized() throws ServletException, IOException {
        String token = "blacklisted.token";
        String header = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(header);
        when(blacklistedTokenRepository.findByToken(token)).thenReturn(Optional.of(new com.cabbooking.model.BlacklistedToken()));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_doesNothing() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
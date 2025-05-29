package com.spring.fortress.vehicles.filters;

import com.spring.fortress.vehicles.config.JwtConfig;
import com.spring.fortress.vehicles.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that authenticates HTTP requests using JWT tokens.
 * Validates tokens and sets the authentication context for authorized users.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    /**
     * Processes incoming HTTP requests to validate JWT tokens and authenticate users.
     * Extracts the token from the Authorization header, validates it, and sets the
     * authentication context if valid.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Processing JWT authentication for request: {}", request.getRequestURI());

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Bearer token found in request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Extract and validate token
        String token = authHeader.substring(7).trim();
        if (token.isBlank()) {
            log.warn("Empty JWT token in request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (JwtUtil.isTokenValid(token, jwtConfig.getSecretKey())) {
                String username = JwtUtil.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authenticated user: {} for request: {}", username, request.getRequestURI());
                } else {
                    log.debug("No authentication set: username is {} or authentication already exists", username);
                }
            } else {
                log.warn("Invalid or expired JWT token for request: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("Failed to process JWT token for request: {}: {}", request.getRequestURI(), e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
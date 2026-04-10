package com.example.temple_billing.config;

import com.example.temple_billing.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        // ✅ SKIP LOGIN ENDPOINT
        if (request.getServletPath().equals("/api/auth/login")) {
            logger.debug("Skipping JWT filter for login endpoint: {}", request.getServletPath());
            chain.doFilter(request, response);
            return;
        }

        // ✅ SKIP PREFLIGHT (VERY IMPORTANT FOR CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("Skipping JWT filter for CORS preflight request");
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request: {} {}", request.getMethod(), request.getServletPath());

        if (StringUtils.hasText(authHeader) &&
                authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            logger.debug("JWT token extracted from Authorization header");

            if (jwtService.validateToken(token)) {

                String username = jwtService.extractUsername(token);
                logger.debug("Token validated for user: {}", username);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                logger.info("Authentication successful for user: {}", username);
            } else {
                logger.warn("JWT token validation failed for Authorization header");
            }
        } else {
            logger.warn("Missing or invalid Authorization header for request: {} {}", request.getMethod(), request.getServletPath());
        }

        chain.doFilter(request, response);
    }
}

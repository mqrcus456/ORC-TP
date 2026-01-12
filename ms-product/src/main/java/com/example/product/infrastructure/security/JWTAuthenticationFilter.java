<<<<<<< HEAD
package com.example.product.infrastructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Ne pas appliquer le filtre sur les endpoints d'actuator/health
        if (requestURI.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token manquant ou mal formaté pour la requête: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            // Valider le token
            jwtValidator.validateToken(token);

            // Extraire les informations utilisateur
            Long userId = jwtValidator.extractUserId(token);
            String email = jwtValidator.extractEmail(token);
            String roles = jwtValidator.extractRoles(token);

            // Créer l'authentification
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    email,
                    token, // Stocker le token dans les credentials pour propagation inter-services
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roles))
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définir dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Token validé pour l'utilisateur: {}", email);

        } catch (JwtException e) {
            log.warn("Token invalide: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
=======
package com.example.product.infrastructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Ne pas appliquer le filtre sur les endpoints d'actuator/health
        if (requestURI.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token manquant ou mal formaté pour la requête: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            // Valider le token
            jwtValidator.validateToken(token);

            // Extraire les informations utilisateur
            Long userId = jwtValidator.extractUserId(token);
            String email = jwtValidator.extractEmail(token);
            String roles = jwtValidator.extractRoles(token);

            // Créer l'authentification
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    email,
                    token, // Stocker le token dans les credentials pour propagation inter-services
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roles))
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définir dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Token validé pour l'utilisateur: {}", email);

        } catch (JwtException e) {
            log.warn("Token invalide: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
>>>>>>> f3e82cd3d266c12596fa197810612cb1aa200954
}
package com.membership.users.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtil {

    private final RSAKeyUtil rsaKeyUtil;

    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLES_CLAIM = "roles";
    private static final int EXPIRATION_HOURS = 1;

    /**
     * Génère un JWT pour un utilisateur.
     */
    public String generateToken(Long userId, String email, String roles) {
        Instant now = Instant.now();
        Instant expiration = now.plus(EXPIRATION_HOURS, ChronoUnit.HOURS);

        RSAPrivateKey privateKey = rsaKeyUtil.loadPrivateKey();

        return Jwts.builder()
                .setSubject(email)
                .claim(USER_ID_CLAIM, userId)
                .claim(EMAIL_CLAIM, email)
                .claim(ROLES_CLAIM, roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Valide un JWT et retourne les claims.
     */
    public Claims validateToken(String token) {
        try {
            RSAPublicKey publicKey = rsaKeyUtil.loadPublicKey();
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token expiré: {}", e.getMessage());
            throw new JwtException("Token expiré", e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("Token invalide: {}", e.getMessage());
            throw new JwtException("Token invalide", e);
        }
    }

    /**
     * Extrait l'ID utilisateur du token.
     */
    public Long extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims.get(USER_ID_CLAIM, Long.class);
    }

    /**
     * Extrait l'email du token.
     */
    public String extractEmail(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    /**
     * Extrait les rôles du token.
     */
    public String extractRoles(String token) {
        Claims claims = validateToken(token);
        return claims.get(ROLES_CLAIM, String.class);
    }

    /**
     * Vérifie si le token est expiré.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
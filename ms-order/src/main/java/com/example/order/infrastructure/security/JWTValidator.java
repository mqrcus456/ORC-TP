package com.example.order.infrastructure.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Component
public class JWTValidator {

    private static final String PUBLIC_KEY_FILE = "public_key.pem";

    /**
     * Charge la clé publique depuis le fichier.
     */
    private RSAPublicKey loadPublicKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(PUBLIC_KEY_FILE));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Erreur lors du chargement de la clé publique", e);
            throw new RuntimeException("Erreur lors du chargement de la clé publique", e);
        }
    }

    /**
     * Valide un JWT et retourne les claims.
     */
    public Claims validateToken(String token) {
        try {
            RSAPublicKey publicKey = loadPublicKey();
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
        return claims.get("userId", Long.class);
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
        return claims.get("roles", String.class);
    }

    /**
     * Vérifie si le token est expiré.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new java.util.Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
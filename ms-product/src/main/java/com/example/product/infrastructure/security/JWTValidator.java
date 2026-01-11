package com.example.product.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class JWTValidator {

    private RSAPublicKey publicKey;

    private static final String MEMBERSHIP_PUBLIC_KEY_URL = "http://localhost:8081/api/v1/public-key";

    /**
     * Récupère la clé publique depuis Membership au démarrage
     */
    @PostConstruct
    public void loadKeyFromMembership() {
        try {
            log.info("Récupération de la clé publique depuis Membership...");
            String keyBase64 = new RestTemplate().getForObject(MEMBERSHIP_PUBLIC_KEY_URL, String.class);
            byte[] decoded = Base64.getDecoder().decode(keyBase64);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) kf.generatePublic(spec);

            log.info("Clé publique récupérée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la clé publique", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Valide le token JWT et retourne les Claims
     */
    public Claims validateToken(String token) {
        try {
            // ✅ Avec JJWT, parser + setSigningKey + build est ok
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token expiré: {}", e.getMessage());
            throw new JwtException("Token expiré", e);
        } catch (JwtException e) {
            log.warn("Token invalide: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extrait l'ID utilisateur du token
     */
    public Long extractUserId(String token) {
        return validateToken(token).get("userId", Long.class);
    }

    /**
     * Extrait l'email du token
     */
    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * Extrait les rôles du token
     */
    public String extractRoles(String token) {
        return validateToken(token).get("roles", String.class);
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getExpiration().before(new java.util.Date());
        } catch (JwtException e) {
            return true;
        }
    }
}

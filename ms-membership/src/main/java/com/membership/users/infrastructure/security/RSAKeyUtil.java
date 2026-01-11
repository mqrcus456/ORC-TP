package com.membership.users.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Component
public class RSAKeyUtil {

    private static final String PRIVATE_KEY_FILE = "private_key.pem";
    private static final String PUBLIC_KEY_FILE = "public_key.pem";
    private static final int KEY_SIZE = 2048;

    /**
     * Génère une paire de clés RSA si elles n'existent pas.
     */
    public void generateKeysIfNotExist() {
        if (Files.exists(Paths.get(PRIVATE_KEY_FILE)) && Files.exists(Paths.get(PUBLIC_KEY_FILE))) {
            log.info("Les clés RSA existent déjà");
            return;
        }

        try {
            log.info("Génération d'une nouvelle paire de clés RSA...");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

            savePrivateKey(privateKey);
            savePublicKey(publicKey);

            log.info("Clés RSA générées et sauvegardées avec succès");
        } catch (NoSuchAlgorithmException e) {
            log.error("Erreur lors de la génération des clés RSA", e);
            throw new RuntimeException("Erreur lors de la génération des clés RSA", e);
        }
    }

    /**
     * Charge la clé privée depuis le fichier.
     */
    public RSAPrivateKey loadPrivateKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Erreur lors du chargement de la clé privée", e);
            throw new RuntimeException("Erreur lors du chargement de la clé privée", e);
        }
    }

    /**
     * Charge la clé publique depuis le fichier.
     */
    public RSAPublicKey loadPublicKey() {
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

    private void savePrivateKey(RSAPrivateKey privateKey) {
        try (FileOutputStream fos = new FileOutputStream(PRIVATE_KEY_FILE)) {
            fos.write(privateKey.getEncoded());
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde de la clé privée", e);
            throw new RuntimeException("Erreur lors de la sauvegarde de la clé privée", e);
        }
    }

    private void savePublicKey(RSAPublicKey publicKey) {
        try (FileOutputStream fos = new FileOutputStream(PUBLIC_KEY_FILE)) {
            fos.write(publicKey.getEncoded());
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde de la clé publique", e);
            throw new RuntimeException("Erreur lors de la sauvegarde de la clé publique", e);
        }
    }
}
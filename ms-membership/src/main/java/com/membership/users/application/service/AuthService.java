package com.membership.users.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.membership.users.application.dto.LoginRequestDTO;
import com.membership.users.application.dto.LoginResponseDTO;
import com.membership.users.domain.entity.User;
import com.membership.users.domain.repository.UserRepository;
import com.membership.users.infrastructure.exception.ResourceNotFoundException;
import com.membership.users.infrastructure.security.JWTUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    /**
     * Authentifie un utilisateur et génère un token JWT.
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérification du mot de passe (en clair pour l'exemple)
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        // Vérification que l'utilisateur est actif
        if (!user.getActive()) {
            throw new IllegalArgumentException("Compte utilisateur désactivé");
        }

        // Génération du token JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), "USER");

        log.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());

        return LoginResponseDTO.builder()
                .token(token)
                .expiresIn(3600)
                .build();
    }
}
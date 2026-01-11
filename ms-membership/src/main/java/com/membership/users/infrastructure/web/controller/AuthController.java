package com.membership.users.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.membership.users.application.dto.LoginRequestDTO;
import com.membership.users.application.dto.LoginResponseDTO;
import com.membership.users.application.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login
     * Authentifie un utilisateur et retourne un token JWT
     *
     * @param loginRequest Les informations de connexion
     * @return Token JWT avec expiration
     */
    @Operation(summary = "Authentification utilisateur",
               description = "Authentifie un utilisateur avec email/mot de passe et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentification réussie",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect",
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content)
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("POST /api/v1/auth/login - Tentative de connexion pour: {}", loginRequest.getEmail());

        try {
            LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Échec d'authentification pour {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
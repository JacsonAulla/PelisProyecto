package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.*;
import com.MediaWeseco.Back.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.MediaWeseco.Back.dtos.AuthResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerInit(@Valid @RequestBody RegisterInitDto dto) {
        try {
            String mensaje = authService.registerInit(dto);
            return ResponseEntity.ok(Map.of("message", mensaje));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDto dto) {
        try {
            authService.verifyCode(dto);
            return ResponseEntity.ok(Map.of("message", "Cuenta verificada correctamente. Ahora crea tu contraseña."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@Valid @RequestBody SetPasswordDto dto) {
        try {
            authService.setPassword(dto);
            return ResponseEntity.ok(Map.of("message", "Contraseña creada. Ya puedes iniciar sesión."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            if (token == null || token.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "Token requerido"));

            return ResponseEntity.ok(authService.loginWithGoogle(token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/complete-google")
    public ResponseEntity<?> completeGoogle(@Valid @RequestBody GoogleCompleteDto dto) {
        try {
            return ResponseEntity.ok(authService.completeGoogleProfile(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
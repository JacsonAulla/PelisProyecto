package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.*;
import com.MediaWeseco.Back.enums.Provider;
import com.MediaWeseco.Back.models.Rol;
import com.MediaWeseco.Back.models.Usuario;
import com.MediaWeseco.Back.repository.RolRepository;
import com.MediaWeseco.Back.repository.UsuarioRepository;
import com.MediaWeseco.Back.service.AuthService;
import com.MediaWeseco.Back.service.EmailService;
import com.MediaWeseco.Back.service.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public String registerInit(RegisterInitDto dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El username ya está ocupado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setUsername(dto.getUsername());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setEstaActivo(true);
        usuario.setEstaVerificado(false);

        usuario.setProvider(Provider.LOCAL);

        Rol rolUsuario = rolRepository.findByNombre("ROLE_USUARIO")
                .orElseThrow(() -> new RuntimeException(
                        "Error: Rol no encontrado. Asegúrate de insertar los roles en la BD."));
        usuario.getRoles().add(rolUsuario);

        String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        usuario.setVerificationCode(codigo);

        usuarioRepository.save(usuario);

        emailService.sendEmail(dto.getEmail(),
                "Verifica tu cuenta en MediaWeseco",
                "Tu código de verificación es: " + codigo);

        return "Usuario registrado. Por favor revisa tu email para verificar el código.";
    }

    @Override
    public void verifyCode(VerifyCodeDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (Boolean.TRUE.equals(usuario.getEstaVerificado())) {
            throw new RuntimeException("El usuario ya está verificado");
        }

        if (usuario.getVerificationCode() == null ||
                !usuario.getVerificationCode().equals(dto.getVerificationCode())) {
            throw new RuntimeException("Error: Código de verificación incorrecto.");
        }

        usuario.setEstaVerificado(true);
        usuario.setVerificationCode(null);
        usuarioRepository.save(usuario);
    }

    @Override
    public void setPassword(SetPasswordDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getEstaVerificado())) {
            throw new RuntimeException("Error: Debes verificar tu email antes de crear una contraseña.");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    public AuthResponse completeGoogleProfile(GoogleCompleteDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioRepository.existsByUsername(dto.getUsername()) &&
                (usuario.getUsername() == null || !usuario.getUsername().equals(dto.getUsername()))) {
            throw new RuntimeException("Username ocupado");
        }

        usuario.setUsername(dto.getUsername());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstaActivo(true);
        usuario.setEstaVerificado(true); // Al completar perfil, asumimos verificado

        usuarioRepository.save(usuario);

        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .role(usuario.getRoles().iterator().next().getNombre())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .imagenUrl(usuario.getImagenUrl())
                .registroCompleto(true)
                .message("Perfil completado")
                .build();
    }

    @Override
    public AuthResponse login(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getIdentifier(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(dto.getIdentifier())
                .or(() -> usuarioRepository.findByUsername(dto.getIdentifier()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (Boolean.FALSE.equals(usuario.getEstaVerificado())) {
            throw new RuntimeException("Tu cuenta no está verificada. Por favor revisa tu correo electrónico.");
        }
        if (Boolean.FALSE.equals(usuario.getEstaActivo())) {
            throw new RuntimeException("Tu cuenta ha sido desactivada. Contacta con soporte.");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .role(usuario.getRoles().iterator().next().getNombre())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .imagenUrl(usuario.getImagenUrl())
                .registroCompleto(true)
                .message("Login exitoso")
                .build();
    }

    @Override
    public AuthResponse loginWithGoogle(String tokenGoogle) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenGoogle);
            if (idToken == null)
                throw new RuntimeException("Token Google inválido");

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");
            String providerId = payload.getSubject();

            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            // CASO A: Nuevo Usuario (Registro Google)
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setProvider(Provider.GOOGLE);
                usuario.setProviderId(providerId);
                usuario.setEstaActivo(true);
                usuario.setEstaVerificado(true);
                usuario.setImagenUrl(pictureUrl);

                Rol rolUsuario = rolRepository.findByNombre("ROLE_USUARIO")
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                usuario.getRoles().add(rolUsuario);

                usuarioRepository.save(usuario);

                return AuthResponse.builder()
                        .email(email)
                        .imagenUrl(pictureUrl)
                        .registroCompleto(false)
                        .message("Usuario nuevo, debe completar perfil")
                        .build();
            }

            // CASO B: Usuario Existente pero Incompleto
            if (usuario.getUsername() == null || usuario.getFechaNacimiento() == null) {
                return AuthResponse.builder()
                        .email(email)
                        .imagenUrl(pictureUrl)
                        .registroCompleto(false)
                        .message("Perfil incompleto")
                        .build();
            }

            // CASO C: Login Exitoso (El usuario YA EXISTE)

            boolean cambios = false;

            // Si en la BD no hay foto, pero Google nos da una, actualizamos
            if (usuario.getImagenUrl() == null && pictureUrl != null) {
                usuario.setImagenUrl(pictureUrl);
                cambios = true;
            }

            // Si en la BD no hay Provider ID, lo vinculamos ahora
            if (usuario.getProviderId() == null && providerId != null) {
                usuario.setProviderId(providerId);
                cambios = true;
            }

            // Si hubo cambios, guardamos en la BD
            if (cambios) {
                usuarioRepository.save(usuario);
            }

            return AuthResponse.builder()
                    .token(jwtService.generateToken(usuario))
                    .role(usuario.getRoles().iterator().next().getNombre())
                    .username(usuario.getUsername())
                    .email(usuario.getEmail())
                    .imagenUrl(usuario.getImagenUrl())
                    .registroCompleto(true)
                    .message("Login Google Exitoso")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error Google: " + e.getMessage());
        }
    }
}
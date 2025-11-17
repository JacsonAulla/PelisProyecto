package com.Proyecto.Peliculas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; // Importante para activar las validaciones
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.Usuario.JwtLoginRespuestaDTO;
import com.Proyecto.Peliculas.dtos.Usuario.LoginDTO;
import com.Proyecto.Peliculas.dtos.Usuario.RegistroDTO;
import com.Proyecto.Peliculas.dtos.Usuario.UsuarioRegistroRespuestaDTO;
import com.Proyecto.Peliculas.security.JwtTokenProvider;
import com.Proyecto.Peliculas.services.UsuarioService;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth") // Todas las URLs de esta clase empiezan con /api/auth
public class AuthController {

    @Autowired
    private UsuarioService usuarioService; // (Nuestro UsuarioServiceImpl)

    @Autowired
    private AuthenticationManager authenticationManager; // (El "Gestor" que definimos en SecurityConfig)

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // (El "Cerebro" que crea tokens)

    // --- 1. Endpoint de Registro ---
    // POST http://localhost:8080/api/auth/register

    @PostMapping("/register")
    public ResponseEntity<UsuarioRegistroRespuestaDTO> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {

        // 1. @Valid: Activa las validaciones (@NotBlank, @Email) del DTO.
        // Si fallan, Spring devuelve un 400 Bad Request automáticamente.

        // 2. @RequestBody: Convierte el JSON de la petición en el objeto RegistroDTO.

        // 3. Llamamos al servicio para que haga la lógica de negocio
        // (hashear password, guardar, etc.)
        UsuarioRegistroRespuestaDTO usuarioRegistrado = usuarioService.registrarUsuario(registroDTO);

        // 4. Devolvemos el DTO de respuesta (SIN password) y un estado 201 Created.
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
    }

    // --- 2. Endpoint de Login ---
    // POST http://localhost:8080/api/auth/login

    @PostMapping("/login")
    public ResponseEntity<JwtLoginRespuestaDTO> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {

        // 1. El "Gestor" de Autenticación (AuthenticationManager)
        // toma las credenciales (loginDTO.getUsernameOrEmail(),
        // loginDTO.getPassword()).
        // 2. INTERNAMENTE, llama a nuestro UsuarioServiceImpl.loadUserByUsername()
        // para buscar al usuario.
        // 3. INTERNAMENTE, usa el PasswordEncoder para comparar la contraseña del DTO
        // con la contraseña hasheada de la Base de Datos.
        // 4. Si algo falla (usuario no existe, contraseña incorrecta),
        // Spring Security lanza una excepción y devuelve un 401 UNAUTHORIZED.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsernameOrEmail(),
                        loginDTO.getPassword()));

        // --- Si el código llega hasta aquí, la autenticación fue EXITOSA ---

        // 5. "Logueamos" al usuario en el contexto de seguridad de Spring
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. Usamos nuestro "Cerebro" (JwtTokenProvider) para crear el token
        String token = jwtTokenProvider.generateToken(authentication);

        // 7. Creamos el DTO de respuesta con el token y lo devolvemos
        JwtLoginRespuestaDTO tokenDTO = new JwtLoginRespuestaDTO(token);

        return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioRegistroRespuestaDTO> getMiPerfil() {
        // Llama al nuevo método del servicio
        UsuarioRegistroRespuestaDTO usuarioDto = usuarioService.getMiPerfil();
        return ResponseEntity.ok(usuarioDto);
    }
}
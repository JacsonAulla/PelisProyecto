package com.Proyecto.Peliculas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // El "escáner"

    @Autowired
    private UserDetailsService userDetailsService; // La "lista de empleados"

    /**
     * Este es el método que se ejecuta en cada petición.
     */
    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // --- 1. Extraer el token de la petición ---
        String token = getTokenFromRequest(request);

        // --- 2. Validar el token ---
        // Si el token existe Y es válido...
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            
            // --- 3. Obtener el username (email) del token ---
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // --- 4. Cargar al usuario desde la Base de Datos ---
            // (Usando nuestro UsuarioServiceImpl)
            // Esto es necesario para obtener los ROLES y detalles frescos del usuario.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // --- 5. Crear el "pase de visitante" (Authentication) ---
            // Le decimos a Spring: "Este usuario es válido, estos son sus permisos (roles)"
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // No necesitamos credenciales (password) aquí
                    userDetails.getAuthorities() // Aquí van los roles (ej. ROLE_ADMIN)
            );

            // Le añadimos detalles extra de la petición (de dónde vino, IP, etc.)
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // --- 6. Guardar al usuario en el Contexto de Seguridad ---
            // ¡ESTA ES LA LÍNEA MÁS IMPORTANTE!
            // "Logueamos" al usuario para ESTA petición.
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // --- 7. Continuar la cadena ---
        // Le decimos al filtro: "Mi trabajo terminó, pasa la petición al
        // siguiente filtro en la cadena (y eventualmente, al Controller)".
        filterChain.doFilter(request, response);
    }

    /**
     * Método ayudante para extraer el token del Header "Authorization".
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // El header debe ser "Authorization: Bearer <token>"
        String bearerToken = request.getHeader("Authorization");

        // Verificamos que el header exista y empiece con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Extraemos solo la cadena del token (quitamos "Bearer ")
            return bearerToken.substring(7, bearerToken.length());
        }

        return null; // No se encontró un token Bearer
    }
}
package com.Proyecto.Peliculas.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.Proyecto.Peliculas.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. Deshabilitar CSRF
                .csrf(csrf -> csrf.disable())

                // 2. ¡NUEVO! Habilitar y configurar CORS aquí mismo
                // Esto reemplaza la necesidad de WebConfig.java
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Definir las reglas de autorización (URLs públicas vs. privadas)
                .authorizeHttpRequests(auth -> auth

                        // 1. REGLA PÚBLICA:
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/generos/**").permitAll()

                        // --- REGLAS PÚBLICAS DE SWAGGER (Corregidas) ---
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll() // La ruta de tu log de error

                        // 2. REGLA DE ADMIN:
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/peliculas").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/generos").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/generos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/generos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/suscripciones/admin/todas").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/ordenes/admin/todas").hasAuthority("ADMIN")

                        // --- Regla General (Autenticados) ---
                        .requestMatchers("/api/suscripciones/**").authenticated()
                        .requestMatchers("/api/ordenes/**").authenticated()
                        .requestMatchers("/api/biblioteca/**").authenticated()

                        .anyRequest().authenticated())

                // 4. Establecer la política de sesión como STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Indicarle a Spring cuál es nuestro "Proveedor de Autenticación"
                .authenticationProvider(authenticationProvider())

                // 6. Añadir nuestro "Guardia" (JwtAuthenticationFilter)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- ¡NUEVO BEAN DE CONFIGURACIÓN DE CORS! ---
    // Aquí definimos las reglas de CORS que antes estaban en WebConfig
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (usamos el wildcard)
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras permitidas
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // --- ¡EL CAMBIO ES ESTA LÍNEA! ---
        // Antes: source.registerCorsConfiguration("/api/**", configuration);
        // Ahora:
        source.registerCorsConfiguration("/**", configuration); // <-- Aplica a TODAS las rutas

        return source;
    }

    // --- 2. El "Proveedor" de Autenticación ---
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder); // Llama a la variable inyectada
        return authProvider;
    }

    // --- 3. El "Gestor" de Autenticación (AuthenticationManager) ---
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
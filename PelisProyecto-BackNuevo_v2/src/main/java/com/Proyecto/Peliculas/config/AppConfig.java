package com.Proyecto.Peliculas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    /**
     * Define el bean PasswordEncoder.
     * Usamos BCrypt, que es el estándar de la industria para hashear contraseñas.
     * Lo movemos aquí para romper la dependencia circular con SecurityConfig.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
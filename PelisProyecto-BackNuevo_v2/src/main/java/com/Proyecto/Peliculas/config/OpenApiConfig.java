package com.Proyecto.Peliculas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Este Bean configura la información general de la API y, lo más importante,
     * el esquema de seguridad (JWT) que usará Swagger UI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        
        final String securitySchemeName = "BearerAuth"; // Un nombre para el esquema

        // 1. Definimos el Esquema de Seguridad (Security Scheme)
        // Esto le dice a Swagger cómo es nuestra seguridad
        SecurityScheme securityScheme = new SecurityScheme()
            .name(securitySchemeName)               // Nombre que usaremos para referenciarlo
            .type(SecurityScheme.Type.HTTP)         // Tipo de seguridad (HTTP)
            .scheme("bearer")                       // El esquema (bearer)
            .bearerFormat("JWT")                    // El formato (JWT)
            .in(SecurityScheme.In.HEADER)           // Dónde va (en el Header)
            .description("Token JWT para autenticación. Ingresar 'Bearer [token]'");

        return new OpenAPI()
            // 2. Añadimos la Información General de la API
            .info(new Info()
                .title("API de PelisProyecto")
                .version("1.0")
                .description("API REST para el proyecto de películas con Spring Boot y Spring Security.")
            )
            // 3. Añadimos el Esquema de Seguridad a los "Componentes"
            .components(
                new Components()
                    .addSecuritySchemes(securitySchemeName, securityScheme)
            )
            // 4. (Opcional pero recomendado) Añadimos el requisito de seguridad globalmente
            // Esto pondrá el "candado" en todos los endpoints
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
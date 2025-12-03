package com.MediaWeseco.Back.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String imagenUrl;
    private String provider; // LOCAL o GOOGLE
    private boolean estaActivo; // Para el semáforo de bloqueo
    private boolean estaVerificado;
    private String role; // "ROLE_USER" o "ROLE_ADMIN"
}
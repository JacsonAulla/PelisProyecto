package com.MediaWeseco.Back.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String username;
    private String email;
    private String imagenUrl;
    private boolean registroCompleto; // true = Login OK, false = Faltan datos
    private String message;
}
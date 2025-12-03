package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GoogleCompleteDto {

    @NotBlank
    @Email
    private String email; // Para identificar a qué usuario de Google actualizamos

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotBlank
    @Size(min = 6)
    private String password; // La contraseña híbrida que pidió el usuario
}
package com.Proyecto.Peliculas.dtos.Usuario;

import com.Proyecto.Peliculas.enums.TipoRol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUsuarioCreateDTO {
    @NotBlank 
    private String nombre;

    @NotBlank 
    private String apellido;

    @NotBlank 
    private String username;

    @NotBlank 
    @Email 
    private String email;

    @NotBlank 
    @Size(min = 6) 
    private String password;

    @NotNull 
    private TipoRol rol;
    
}

package com.Proyecto.Peliculas.dtos.Usuario;

import com.Proyecto.Peliculas.enums.TipoRol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroRespuestaDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private TipoRol rol;
}
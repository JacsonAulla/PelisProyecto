package com.Proyecto.Peliculas.dtos.Peliculas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaUsuarioDTO {
    private Long id;
    private String titulo;
    private String imgFrente;
}
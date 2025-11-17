package com.Proyecto.Peliculas.dtos.imagen;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagenPeliculaDTO {
    private Long id;
    private String urlImagen;
}
package com.Proyecto.Peliculas.dtos.imagen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenCrearDTO {
    private String urlImagen;
    private Long peliculaId;
}
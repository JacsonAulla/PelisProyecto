package com.Proyecto.Peliculas.dtos.Peliculas;

import java.math.BigDecimal;
import java.util.List;

import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;
import com.Proyecto.Peliculas.dtos.imagen.ImagenPeliculaDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PeliculaDetalleDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Integer anioLanzamiento;
    private Integer duracionMinutos;
    private BigDecimal precioComprar;
    private String urlStream;

    private String imgFrente;

    // Aquí sí cargamos las relaciones, usando los DTOs de soporte
    private List<GeneroDTO> generos;
    private List<ImagenPeliculaDTO> imagenes;
}
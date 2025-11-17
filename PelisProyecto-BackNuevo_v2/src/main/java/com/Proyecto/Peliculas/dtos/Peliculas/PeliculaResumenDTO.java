package com.Proyecto.Peliculas.dtos.Peliculas;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PeliculaResumenDTO {
    private Long id;
    private String titulo;
    private String imgFrente; // La portada
    private Integer anioLanzamiento;
    private BigDecimal precioComprar;
}
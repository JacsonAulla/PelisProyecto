package com.Proyecto.Peliculas.dtos.Peliculas;

import java.math.BigDecimal;
import java.util.List;

import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaAdminDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Integer anioLanzamiento;
    private Integer duracionMinutos;
    private Boolean disponible;
    private BigDecimal precioComprar;
    private String imgFrente;
    private String urlStream;
    private List<GeneroDTO> generos;

}
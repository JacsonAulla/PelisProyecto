package com.Proyecto.Peliculas.dtos.compra;

import java.math.BigDecimal;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetalleOrdenResponseDTO {
    private Long id;
    private BigDecimal precio; // El precio al que se compró
    
    // Incluimos un resumen de la película, no el objeto completo
    // para evitar bucles y mantenerlo ligero.
    private PeliculaResumenDTO pelicula; 
}
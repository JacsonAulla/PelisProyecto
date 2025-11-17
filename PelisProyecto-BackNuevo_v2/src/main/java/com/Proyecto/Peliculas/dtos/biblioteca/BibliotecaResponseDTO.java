package com.Proyecto.Peliculas.dtos.biblioteca;

import java.time.LocalDateTime;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BibliotecaResponseDTO {
    
    private Long idBiblioteca; // El ID de la entrada en usuario_pelicula
    private LocalDateTime fechaCompra;
    
    // Usamos el DTO resumen que ya tenemos
    private PeliculaResumenDTO pelicula; 
}
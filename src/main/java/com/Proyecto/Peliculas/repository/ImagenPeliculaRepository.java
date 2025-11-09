package com.Proyecto.Peliculas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.ImagenPelicula;

@Repository
public interface ImagenPeliculaRepository extends JpaRepository<ImagenPelicula, Long> {
    
    // --- Para el Usuario ---
    
    //nada
    
    // --- Para el Admin ---

    // Obtener todas las imágenes de una película
    List<ImagenPelicula> findByPeliculaId(Long peliculaId);
    
    // Crear imagen
    // ImagenPelicula save(ImagenPelicula imagen);
    
    // Actualizar imagen
    // ImagenPelicula save(ImagenPelicula imagen);
    
    // Eliminar imagen por ID 
    // void deleteById(Long id);
    
    // Eliminar todas las imágenes de una película
    void deleteByPeliculaId(Long peliculaId);
    
    // Eliminar imagen
    // void delete(ImagenPelicula imagen);
}

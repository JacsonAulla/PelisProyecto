package com.Proyecto.Peliculas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.Pelicula;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long>{

    // --- Para el Usuario ---

    //para obtener todos las peliculas que es mostrara en el index List<Pelicula> findAll(); este es por defecto por eso no lo coloque

    // Obtener película por nombre/título
    Optional<Pelicula> findByTitulo(String titulo);
    
    // --- Para el Admin ---

    //obtener las peliculas con estado disponible(true)
    List<Pelicula> findByDisponibleTrue();

    //para obtener todas las peliculas findAll(); ya lo usa el usuario pero igual es para admin p

    //para obtener pelicula por su id Optional<Pelicula> findById(Long id); este es por defecto tambien

    // Crear película tambien por defecto
    // Pelicula save(Pelicula pelicula);

    // Editar película tambien por defecto
    // Pelicula save(Pelicula pelicula);
    
    // Eliminar película por ID tambien por defecto
    // void deleteById(Long id);
    
    // Eliminar película tambien por defecto
    // void delete(Pelicula pelicula);
}

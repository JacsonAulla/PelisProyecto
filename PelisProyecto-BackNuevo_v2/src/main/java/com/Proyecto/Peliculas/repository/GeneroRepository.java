package com.Proyecto.Peliculas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.Genero;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long>{

    Optional<Genero> findByNombreIgnoreCase(String nombre);

    // --- Para el Usuario ---

    //no hay nada aun

    // --- Para el Admin ---

    //para obtener todos las generos List<Pelicula> findAll();

    // Obtener género por ID
    // Optional<Genero> findById(Long id);

    // Buscar género por nombre
    Optional<Genero> findByNombre(String nombre);

    // Crear género  tambien por de fecto
    // Género  save(Genero genero);

    // Editar género  tambien por defecto
    // Género  save(Genero genero);
    
    // Eliminar género  por ID tambien por defecto
    // void deleteById(Long id);
    
    // Eliminar género  tambien por defecto
    // void delete(Genero genero);

}

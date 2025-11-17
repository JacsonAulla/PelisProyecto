package com.Proyecto.Peliculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.UsuarioPelicula;

@Repository
public interface UsuarioPeliculaRepository extends JpaRepository<UsuarioPelicula, Long> {

    // Para la biblioteca del usuario
    Page<UsuarioPelicula> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    // Para verificar si el usuario ya posee una película
    boolean existsByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);
}
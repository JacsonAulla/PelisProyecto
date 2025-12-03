package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Pelicula;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    Optional<Pelicula> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = { "generos" })
    Page<Pelicula> findByEstaActivoTrue(Pageable pageable);
}
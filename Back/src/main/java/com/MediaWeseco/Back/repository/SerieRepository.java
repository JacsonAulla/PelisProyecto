package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Serie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Para el público (solo activas)
    @EntityGraph(attributePaths = { "generos", "temporadas" })
    Page<Serie> findByEstaActivoTrue(Pageable pageable);
}
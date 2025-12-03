package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Temporada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemporadaRepository extends JpaRepository<Temporada, Long> {
    // Buscar temporadas por ID de serie (útil para validaciones)
    List<Temporada> findBySerieId(Long serieId);
}
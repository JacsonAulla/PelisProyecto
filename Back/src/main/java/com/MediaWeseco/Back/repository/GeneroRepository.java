package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Integer> {
    // buscar por nombre
    Optional<Genero> findByNombre(String nombre);

    // verificar existencia rápida
    boolean existsByNombre(String nombre);
}
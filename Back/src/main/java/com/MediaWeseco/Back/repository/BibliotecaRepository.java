package com.MediaWeseco.Back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MediaWeseco.Back.models.Biblioteca;

import java.util.List;

@Repository
public interface BibliotecaRepository extends JpaRepository<Biblioteca, Long> {
    List<Biblioteca> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndContenidoId(Long usuarioId, Long contenidoId);
}
package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoContenidoRepository extends JpaRepository<TipoContenido, Integer> {
    Optional<TipoContenido> findByNombre(String nombre);
}
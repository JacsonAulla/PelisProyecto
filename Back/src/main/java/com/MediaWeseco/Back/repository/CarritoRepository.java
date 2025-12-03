package com.MediaWeseco.Back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MediaWeseco.Back.models.ItemCarrito;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<ItemCarrito, Long> {
    List<ItemCarrito> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId); // Para vaciar carrito al comprar

    boolean existsByUsuarioIdAndContenidoId(Long usuarioId, Long contenidoId);
}
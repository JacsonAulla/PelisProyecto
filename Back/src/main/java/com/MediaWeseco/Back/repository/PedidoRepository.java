package com.MediaWeseco.Back.repository;

import com.MediaWeseco.Back.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Para que un usuario pueda ver su historial de compras
    List<Pedido> findByUsuarioId(Long usuarioId);
}
package com.Proyecto.Peliculas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.enums.EstadoOrden;
import com.Proyecto.Peliculas.models.OrdenCompra;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    
    // Para el historial de compras del usuario
    Page<OrdenCompra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId, Pageable pageable);
    
    // ========== USUARIO (Consultas públicas) ==========
    
    // Obtener órdenes de un usuario ordenadas por fecha descendente
    List<OrdenCompra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
    
    // Obtener órdenes completadas de un usuario
    List<OrdenCompra> findByUsuarioIdAndEstado(Long usuarioId, EstadoOrden estado);
    
    // Obtener órdenes pendientes de un usuario
    List<OrdenCompra> findByUsuarioIdAndEstadoOrderByFechaCompraDesc(Long usuarioId, EstadoOrden estado);
    
    
    // ========== ADMIN (Consultas administrativas) ==========
    
    // Obtener todas las órdenes (ya se usa findAll() de JpaRepository)
    // List<OrdenCompra> findAll();
    
    // Obtener órdenes completadas
    List<OrdenCompra> findByEstado(EstadoOrden estado);
    
    // Obtener órdenes por rango de fechas
    List<OrdenCompra> findByFechaCompraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Obtener órdenes por estado específico (ya cubierto por findByEstado())
    // List<OrdenCompra> findByEstado(EstadoOrden estado);
    
    
    // ========== TRANSACCIONAL (Crear, editar, eliminar) ==========
    
    // Crear orden (ya se usa save() de JpaRepository)
    // OrdenCompra save(OrdenCompra orden);
    
    // Editar orden (ya se usa save() de JpaRepository)
    // OrdenCompra save(OrdenCompra orden);
    
    // Eliminar orden por ID (ya se usa deleteById() de JpaRepository)
    // void deleteById(Long id);
    
    // Eliminar orden (ya se usa delete() de JpaRepository)
    // void delete(OrdenCompra orden);
}

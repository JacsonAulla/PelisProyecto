package com.Proyecto.Peliculas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.DetalleOrden;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
    // ========== USUARIO (Consultas públicas) ==========
    
    // Obtener detalles de una orden específica
    List<DetalleOrden> findByOrdenId(Long ordenId);
    
    
    // ========== ADMIN (Consultas administrativas) ==========
    
    // Obtener detalles de una película en todas las órdenes
    List<DetalleOrden> findByPeliculaId(Long peliculaId);
    
    
    // ========== TRANSACCIONAL (Crear, editar, eliminar) ==========
    
    // Crear detalle orden (ya se usa save() de JpaRepository)
    // DetalleOrden save(DetalleOrden detalle);
    
    // Editar detalle orden (ya se usa save() de JpaRepository)
    // DetalleOrden save(DetalleOrden detalle);
    
    // Eliminar detalle orden por ID (ya se usa deleteById() de JpaRepository)
    // void deleteById(Long id);
    
    // Eliminar detalles de una orden (si es necesario en cascada)
    void deleteByOrdenId(Long ordenId);
    
    // Eliminar detalle orden (ya se usa delete() de JpaRepository)
    // void delete(DetalleOrden detalle);

    // Obtener detalles de una orden específica, cargar película con EAGER fetch
    @Query("SELECT d FROM DetalleOrden d JOIN FETCH d.pelicula WHERE d.orden.id = :ordenId")
    List<DetalleOrden> findByOrdenIdWithPelicula(@Param("ordenId") Long ordenId);
}


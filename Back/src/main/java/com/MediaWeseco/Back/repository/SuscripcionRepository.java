package com.MediaWeseco.Back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MediaWeseco.Back.models.Suscripcion;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    // Buscar la última suscripción activa
    @Query("SELECT s FROM Suscripcion s WHERE s.usuario.id = :userId AND s.estaActiva = true AND s.fechaFin > CURRENT_TIMESTAMP")
    Optional<Suscripcion> findActiveSubscription(@Param("userId") Long userId);
}
package com.Proyecto.Peliculas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.enums.EstadoSuscripcion;
import com.Proyecto.Peliculas.models.Suscripcion;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    /**
     * Busca una suscripción por ID de usuario y un estado específico.
     * Lo usaremos para:
     * 1. (Validar) Encontrar si el usuario ya tiene una suscripción ACTIVA.
     * 2. (Cancelar) Encontrar la suscripción ACTIVA del usuario para cancelarla.
     */
    Optional<Suscripcion> findByUsuarioIdAndEstado(Long usuarioId, EstadoSuscripcion estado);

    /**
     * Busca la suscripción más reciente de un usuario (activa o no).
     * Lo usaremos para "Obtener mi estado de suscripción".
     */
    Optional<Suscripcion> findFirstByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);
}

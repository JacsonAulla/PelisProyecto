package com.Proyecto.Peliculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionRequestDTO;
import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionResponseDTO;

public interface SuscripcionService {

    /**
     * Obtiene el estado de la suscripción actual (o más reciente)
     * del USUARIO LOGUEADO.
     */
    SuscripcionResponseDTO obtenerMiEstadoSuscripcion();

    /**
     * Crea una nueva suscripción (activa por 1 mes) para
     * el USUARIO LOGUEADO.
     */
    SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO requestDTO);

    /**
     * Cancela la suscripción ACTIVA del USUARIO LOGUEADO.
     */
    SuscripcionResponseDTO cancelarSuscripcion();

    /**
     * (Admin) Obtiene una lista paginada de TODAS las suscripciones
     * de todos los usuarios.
     */
    Page<SuscripcionResponseDTO> obtenerTodasLasSuscripciones(Pageable pageable);
}


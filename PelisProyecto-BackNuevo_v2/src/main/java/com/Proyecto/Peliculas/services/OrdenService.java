package com.Proyecto.Peliculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Proyecto.Peliculas.dtos.compra.OrdenRequestDTO;
import com.Proyecto.Peliculas.dtos.compra.OrdenResponseDTO;

public interface OrdenService {

    // --- MÉTODOS DE USUARIO ---

    /**
     * Crea una nueva orden de compra (carrito) para el usuario logueado.
     * Esto también simulará el 'trigger' y añadirá las películas
     * a la biblioteca del usuario.
     */
    OrdenResponseDTO crearOrden(OrdenRequestDTO ordenRequestDTO);

    /**
     * Obtiene el historial de órdenes del usuario logueado, paginado.
     */
    Page<OrdenResponseDTO> obtenerMisOrdenes(Pageable pageable);

    /**
     * Obtiene el detalle de una orden específica, verificando
     * que pertenezca al usuario logueado.
     */
    OrdenResponseDTO obtenerOrdenPorId(Long ordenId);

    // --- MÉTODO DE ADMIN ---
    
    /**
     * Obtiene una lista paginada de TODAS las órdenes de TODOS los usuarios. (Admin)
     */
    Page<OrdenResponseDTO> obtenerTodasLasOrdenes(Pageable pageable);
}
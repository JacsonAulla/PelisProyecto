package com.Proyecto.Peliculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Proyecto.Peliculas.dtos.biblioteca.BibliotecaResponseDTO;

public interface BibliotecaService {

    /**
     * Obtiene la biblioteca de películas (compradas) del usuario
     * logueado, de forma paginada.
     */
    Page<BibliotecaResponseDTO> obtenerMiBiblioteca(Pageable pageable);
}
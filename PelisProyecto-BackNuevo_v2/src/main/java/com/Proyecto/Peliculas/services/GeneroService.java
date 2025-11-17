package com.Proyecto.Peliculas.services;

import java.util.List;

import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;

public interface GeneroService {

    //Devuelve la lista de todos los géneros.
    List<GeneroDTO> obtenerTodosLosGeneros();

    /**
     * Obtiene un género por su ID. (Público)
     */
    GeneroDTO obtenerGeneroPorId(Long id);

    /**
     * Crea un nuevo género. (Admin)
     */
    GeneroDTO crearGenero(GeneroDTO generoDTO);

    /**
     * Actualiza el nombre de un género existente. (Admin)
     */
    GeneroDTO actualizarGenero(Long id, GeneroDTO generoDTO);

    /**
     * Elimina un género. (Admin)
     */
    void eliminarGenero(Long id);
}
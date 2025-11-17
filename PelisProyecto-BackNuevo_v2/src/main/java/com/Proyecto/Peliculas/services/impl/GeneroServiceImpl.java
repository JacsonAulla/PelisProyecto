package com.Proyecto.Peliculas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;
import com.Proyecto.Peliculas.exceptions.ResourceNotFoundException;
import com.Proyecto.Peliculas.models.Genero;
import com.Proyecto.Peliculas.repository.GeneroRepository;
import com.Proyecto.Peliculas.services.GeneroService;

@Service
public class GeneroServiceImpl implements GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    // --- Mapeador Helper ---
    // (Convierte Entidad <-> DTO)
    private GeneroDTO mapEntidadToDTO(Genero genero) {
        GeneroDTO dto = new GeneroDTO();
        dto.setId(genero.getId());
        dto.setNombre(genero.getNombre());
        return dto;
    }

    // --- MÉTODOS PÚBLICOS ---

    @Override
    @Transactional(readOnly = true)
    public List<GeneroDTO> obtenerTodosLosGeneros() {
        return generoRepository.findAll().stream()
                .map(this::mapEntidadToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroDTO obtenerGeneroPorId(Long id) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genero", "id", id));
        return mapEntidadToDTO(genero);
    }

    // --- MÉTODOS DE ADMIN ---

    @Override
    @Transactional
    public GeneroDTO crearGenero(GeneroDTO generoDTO) {
        // 1. Validar duplicados
        generoRepository.findByNombreIgnoreCase(generoDTO.getNombre())
            .ifPresent(g -> { 
                throw new RuntimeException("Ya existe un género con el nombre: " + g.getNombre()); 
            });

        // 2. Crear entidad
        Genero genero = new Genero();
        genero.setNombre(generoDTO.getNombre());
        
        // 3. Guardar
        Genero generoGuardado = generoRepository.save(genero);
        
        // 4. Devolver DTO
        return mapEntidadToDTO(generoGuardado);
    }

    @Override
    @Transactional
    public GeneroDTO actualizarGenero(Long id, GeneroDTO generoDTO) {
        // 1. Buscar el género existente
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genero", "id", id));

        // 2. Validar duplicados (solo si el nombre cambió)
        if (!genero.getNombre().equalsIgnoreCase(generoDTO.getNombre())) {
            generoRepository.findByNombreIgnoreCase(generoDTO.getNombre())
                .ifPresent(g -> { 
                    throw new RuntimeException("El nombre '" + g.getNombre() + "' ya está en uso por otro género."); 
                });
        }
        
        // 3. Actualizar
        genero.setNombre(generoDTO.getNombre());
        Genero generoActualizado = generoRepository.save(genero);
        
        // 4. Devolver DTO
        return mapEntidadToDTO(generoActualizado);
    }

    @Override
    @Transactional
    public void eliminarGenero(Long id) {
        // 1. Buscar
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genero", "id", id));
        
        // 2. Advertencia de Lógica de Negocio:
        // Eliminar un género puede ser problemático si hay películas
        // asociadas a él (puede causar errores o dejar películas huérfanas).
        // En un sistema real, aquí iría lógica para reasignar películas
        // o prohibir el borrado si está en uso.
        // Por ahora, confiamos en el ON DELETE CASCADE de la BD
        // (que quitará la relación en 'pelicula_genero').
        
        // 3. Eliminar
        generoRepository.delete(genero);
    }
}
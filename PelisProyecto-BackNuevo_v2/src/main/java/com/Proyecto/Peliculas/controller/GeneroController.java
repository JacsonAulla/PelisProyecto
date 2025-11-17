package com.Proyecto.Peliculas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;
import com.Proyecto.Peliculas.services.GeneroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/generos")
// (Configuración CORS global ya aplicada en WebConfig)
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    // --- ENDPOINTS PÚBLICOS (GET) ---

    /**
     * GET /api/generos
     * Obtiene la lista completa de géneros.
     */
    @GetMapping
    public ResponseEntity<List<GeneroDTO>> obtenerTodos() {
        return new ResponseEntity<>(generoService.obtenerTodosLosGeneros(), HttpStatus.OK);
    }

    /**
     * GET /api/generos/{id}
     * Obtiene un género específico por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GeneroDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(generoService.obtenerGeneroPorId(id), HttpStatus.OK);
    }

    // --- ENDPOINTS DE ADMIN (POST, PUT, DELETE) ---

    /**
     * POST /api/generos
     * Crea un nuevo género. (Solo Admin)
     */
    @PostMapping
    public ResponseEntity<GeneroDTO> crearGenero(@Valid @RequestBody GeneroDTO generoDTO) {
        GeneroDTO nuevoGenero = generoService.crearGenero(generoDTO);
        return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED);
    }

    /**
     * PUT /api/generos/{id}
     * Actualiza un género existente. (Solo Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<GeneroDTO> actualizarGenero(
            @PathVariable Long id,
            @Valid @RequestBody GeneroDTO generoDTO) {
        
        GeneroDTO generoActualizado = generoService.actualizarGenero(id, generoDTO);
        return new ResponseEntity<>(generoActualizado, HttpStatus.OK);
    }

    /**
     * DELETE /api/generos/{id}
     * Elimina un género. (Solo Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGenero(@PathVariable Long id) {
        generoService.eliminarGenero(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
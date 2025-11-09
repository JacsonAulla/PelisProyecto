package com.Proyecto.Peliculas.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.models.Genero;
import com.Proyecto.Peliculas.services.GeneroService;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {
    
    @Autowired
    private GeneroService generoService;

    // Obtener todos los géneros
    @GetMapping
    public List<Genero> listarTodos() {
        return generoService.obtenerTodos();
    }

    // Obtener género por ID
    @GetMapping("/{id}")
    public ResponseEntity<Genero> buscarPorId(@PathVariable Long id) {
        Optional<Genero> genero = generoService.obtenerPorId(id);
        return genero.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener género por nombre
    @GetMapping("/buscar")
    public ResponseEntity<Genero> buscarPorNombre(@RequestParam String nombre) {
        Optional<Genero> genero = generoService.obtenerPorNombre(nombre);
        return genero.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear género
    @PostMapping
    public ResponseEntity<Genero> crear(@RequestBody Genero genero) {
        Genero nuevo = generoService.crearGenero(genero);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Editar género
    @PutMapping("/{id}")
    public ResponseEntity<Genero> actualizar(@PathVariable Long id, @RequestBody Genero datosActualizados) {
        Genero actualizado = generoService.actualizarGenero(id, datosActualizados);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar género
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        generoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
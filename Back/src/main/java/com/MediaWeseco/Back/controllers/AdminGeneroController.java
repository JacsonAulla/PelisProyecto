package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.GeneroDto;
import com.MediaWeseco.Back.service.GeneroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/generos")
@RequiredArgsConstructor
public class AdminGeneroController {

    private final GeneroService generoService;

    // GET: Listar todos
    @GetMapping
    public ResponseEntity<List<GeneroDto>> getAll() {
        return ResponseEntity.ok(generoService.getAllGeneros());
    }

    // POST: Crear
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GeneroDto dto) {
        try {
            return ResponseEntity.ok(generoService.createGenero(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT: Editar
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody GeneroDto dto) {
        try {
            return ResponseEntity.ok(generoService.updateGenero(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE: Borrar
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            generoService.deleteGenero(id);
            return ResponseEntity.ok(Map.of("message", "Género eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

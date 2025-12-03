package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.PeliculaDto;
import com.MediaWeseco.Back.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/peliculas")
@RequiredArgsConstructor
public class AdminPeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<Page<PeliculaDto>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(peliculaService.getAllPeliculas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(peliculaService.getPeliculaById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PeliculaDto dto) {
        try {
            return ResponseEntity.ok(peliculaService.createPelicula(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PeliculaDto dto) {
        try {
            return ResponseEntity.ok(peliculaService.updatePelicula(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            peliculaService.deletePelicula(id);
            return ResponseEntity.ok(Map.of("message", "Película eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
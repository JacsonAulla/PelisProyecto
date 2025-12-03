package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.PeliculaDto;
import com.MediaWeseco.Back.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PublicPeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<Page<PeliculaDto>> getAll(
            @PageableDefault(size = 12, sort = "anioLanzamiento") Pageable pageable) {

        return ResponseEntity.ok(peliculaService.getActivePeliculas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(peliculaService.getPeliculaById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
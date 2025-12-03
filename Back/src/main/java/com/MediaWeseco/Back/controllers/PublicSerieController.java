package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.SerieDto;
import com.MediaWeseco.Back.service.SerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
public class PublicSerieController {

    private final SerieService serieService;

    // GET /series
    @GetMapping
    public ResponseEntity<Page<SerieDto>> getAll(
            @PageableDefault(size = 12, sort = "anioLanzamiento") Pageable pageable) {
        return ResponseEntity.ok(serieService.getActiveSeries(pageable));
    }

    // GET /series/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serieService.getSerieById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
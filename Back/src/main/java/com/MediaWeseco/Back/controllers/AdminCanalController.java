package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.CanalDto;
import com.MediaWeseco.Back.service.CanalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/canales")
@RequiredArgsConstructor
public class AdminCanalController {
    private final CanalService canalService;

    @GetMapping
    public ResponseEntity<Page<CanalDto>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(canalService.getAllCanales(pageable));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CanalDto dto) {
        try {
            return ResponseEntity.ok(canalService.createCanal(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CanalDto dto) {
        try {
            return ResponseEntity.ok(canalService.updateCanal(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            canalService.deleteCanal(id);
            return ResponseEntity.ok(Map.of("message", "Canal eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
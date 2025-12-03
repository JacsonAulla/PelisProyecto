package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.dtos.CanalDto;
import com.MediaWeseco.Back.service.CanalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/canales")
@RequiredArgsConstructor
public class PublicCanalController {
    private final CanalService canalService;

    @GetMapping
    public ResponseEntity<Page<CanalDto>> getAll(@PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(canalService.getActiveCanales(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(canalService.getCanalById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
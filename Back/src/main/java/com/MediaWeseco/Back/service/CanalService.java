package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.CanalDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CanalService {
    Page<CanalDto> getAllCanales(Pageable pageable); // Admin

    Page<CanalDto> getActiveCanales(Pageable pageable); // Público

    CanalDto getCanalById(Long id);

    CanalDto createCanal(CanalDto dto);

    CanalDto updateCanal(Long id, CanalDto dto);

    void deleteCanal(Long id);
}
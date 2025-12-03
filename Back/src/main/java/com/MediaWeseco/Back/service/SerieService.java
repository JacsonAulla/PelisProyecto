package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.SerieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SerieService {

    // Admin
    Page<SerieDto> getAllSeries(Pageable pageable);

    SerieDto createSerie(SerieDto dto);

    SerieDto updateSerie(Long id, SerieDto dto);

    void deleteSerie(Long id);

    SerieDto getSerieById(Long id);

    // Público
    Page<SerieDto> getActiveSeries(Pageable pageable);
}
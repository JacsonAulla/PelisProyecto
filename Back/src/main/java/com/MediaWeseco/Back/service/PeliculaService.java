package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.PeliculaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeliculaService {
    Page<PeliculaDto> getAllPeliculas(Pageable pageable);

    PeliculaDto getPeliculaById(Long id);

    PeliculaDto createPelicula(PeliculaDto dto);

    PeliculaDto updatePelicula(Long id, PeliculaDto dto);

    void deletePelicula(Long id);

    Page<PeliculaDto> getActivePeliculas(Pageable pageable);
}
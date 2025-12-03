package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.dtos.GeneroDto;
import java.util.List;

public interface GeneroService {
    List<GeneroDto> getAllGeneros();

    GeneroDto createGenero(GeneroDto dto);

    GeneroDto updateGenero(Integer id, GeneroDto dto);

    void deleteGenero(Integer id);
}
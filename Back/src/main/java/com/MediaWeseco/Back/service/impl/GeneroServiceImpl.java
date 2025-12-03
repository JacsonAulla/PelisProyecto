package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.GeneroDto;
import com.MediaWeseco.Back.models.Genero;
import com.MediaWeseco.Back.repository.GeneroRepository;
import com.MediaWeseco.Back.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;

    @Override
    public List<GeneroDto> getAllGeneros() {
        return generoRepository.findAll().stream()
                .map(g -> new GeneroDto(g.getId(), g.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public GeneroDto createGenero(GeneroDto dto) {
        // Validación de duplicados
        if (generoRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("El género '" + dto.getNombre() + "' ya existe.");
        }

        Genero genero = new Genero();
        genero.setNombre(dto.getNombre());

        Genero guardado = generoRepository.save(genero);
        return new GeneroDto(guardado.getId(), guardado.getNombre());
    }

    @Override
    public GeneroDto updateGenero(Integer id, GeneroDto dto) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));

        if (!genero.getNombre().equalsIgnoreCase(dto.getNombre())
                && generoRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("El género '" + dto.getNombre() + "' ya existe.");
        }

        genero.setNombre(dto.getNombre());
        Genero actualizado = generoRepository.save(genero);
        return new GeneroDto(actualizado.getId(), actualizado.getNombre());
    }

    @Override
    public void deleteGenero(Integer id) {
        if (!generoRepository.existsById(id)) {
            throw new RuntimeException("Género no encontrado");
        }
        generoRepository.deleteById(id);
    }
}
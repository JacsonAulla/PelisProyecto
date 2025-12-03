package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.GeneroDto;
import com.MediaWeseco.Back.dtos.PeliculaDto;
import com.MediaWeseco.Back.models.Genero;
import com.MediaWeseco.Back.models.Pelicula;
import com.MediaWeseco.Back.models.TipoContenido;
import com.MediaWeseco.Back.repository.GeneroRepository;
import com.MediaWeseco.Back.repository.PeliculaRepository;
import com.MediaWeseco.Back.repository.TipoContenidoRepository;
import com.MediaWeseco.Back.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final TipoContenidoRepository tipoContenidoRepository;
    private final GeneroRepository generoRepository;

    @Override
    public Page<PeliculaDto> getAllPeliculas(Pageable pageable) {
        return peliculaRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public PeliculaDto getPeliculaById(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
        return mapToDto(pelicula);
    }

    @Override
    @Transactional
    public PeliculaDto createPelicula(PeliculaDto dto) {
        if (peliculaRepository.existsBySlug(dto.getSlug())) {
            throw new RuntimeException("El slug '" + dto.getSlug() + "' ya está en uso.");
        }

        Pelicula pelicula = new Pelicula();

        // 1. Asignar Tipo (Obligatorio)
        TipoContenido tipo = tipoContenidoRepository.findByNombre("PELICULA")
                .orElseThrow(
                        () -> new RuntimeException("Error: Tipo 'PELICULA' no existe en BD. ¿Ejecutaste el INSERT?"));
        pelicula.setTipoContenido(tipo);

        // 2. Mapear datos
        mapDtoToEntity(dto, pelicula);

        // 3. Guardar
        Pelicula guardada = peliculaRepository.save(pelicula);
        return mapToDto(guardada);
    }

    @Override
    @Transactional
    public PeliculaDto updatePelicula(Long id, PeliculaDto dto) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        if (!pelicula.getSlug().equals(dto.getSlug()) && peliculaRepository.existsBySlug(dto.getSlug())) {
            throw new RuntimeException("El slug '" + dto.getSlug() + "' ya está en uso.");
        }

        mapDtoToEntity(dto, pelicula);
        return mapToDto(peliculaRepository.save(pelicula));
    }

    @Override
    public void deletePelicula(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        pelicula.setEstaActivo(false);
        peliculaRepository.save(pelicula);
    }

    private void mapDtoToEntity(PeliculaDto dto, Pelicula entity) {
        entity.setTitulo(dto.getTitulo());
        entity.setSlug(dto.getSlug());
        entity.setDescripcion(dto.getDescripcion());
        entity.setAnioLanzamiento(dto.getAnioLanzamiento());

        entity.setImgPoster(dto.getImgPortada());
        entity.setImgBanner(dto.getImgBanner());

        entity.setPrecio(dto.getPrecio());
        if (dto.getEstaActivo() != null)
            entity.setEstaActivo(dto.getEstaActivo());

        entity.setDuracionMinutos(dto.getDuracionMinutos());
        entity.setUrlStream(dto.getUrlStream());

        if (dto.getGenerosIds() != null) {
            List<Genero> generos = generoRepository.findAllById(dto.getGenerosIds());
            entity.setGeneros(new HashSet<>(generos));
        }
    }

    private PeliculaDto mapToDto(Pelicula entity) {
        List<GeneroDto> generosDto = entity.getGeneros().stream()
                .map(g -> new GeneroDto(g.getId(), g.getNombre()))
                .collect(Collectors.toList());

        return PeliculaDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .slug(entity.getSlug())
                .descripcion(entity.getDescripcion())
                .anioLanzamiento(entity.getAnioLanzamiento())

                .imgPortada(entity.getImgPoster())
                .imgBanner(entity.getImgBanner())

                .precio(entity.getPrecio())
                .estaActivo(entity.getEstaActivo())
                .duracionMinutos(entity.getDuracionMinutos())
                .urlStream(entity.getUrlStream())
                .generos(generosDto)
                .build();
    }

    @Override
    public Page<PeliculaDto> getActivePeliculas(Pageable pageable) {
        return peliculaRepository.findByEstaActivoTrue(pageable)
                .map(this::mapToDto);
    }
}
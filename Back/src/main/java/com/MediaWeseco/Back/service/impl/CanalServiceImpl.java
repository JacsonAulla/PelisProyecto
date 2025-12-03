package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.CanalDto;
import com.MediaWeseco.Back.dtos.GeneroDto;
import com.MediaWeseco.Back.models.Canal;
import com.MediaWeseco.Back.models.Genero;
import com.MediaWeseco.Back.models.TipoContenido;
import com.MediaWeseco.Back.repository.CanalRepository;
import com.MediaWeseco.Back.repository.GeneroRepository;
import com.MediaWeseco.Back.repository.TipoContenidoRepository;
import com.MediaWeseco.Back.service.CanalService;
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
public class CanalServiceImpl implements CanalService {

    private final CanalRepository canalRepository;
    private final TipoContenidoRepository tipoContenidoRepository;
    private final GeneroRepository generoRepository;

    @Override
    public Page<CanalDto> getAllCanales(Pageable pageable) {
        return canalRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<CanalDto> getActiveCanales(Pageable pageable) {
        return canalRepository.findByEstaActivoTrue(pageable).map(this::mapToDto);
    }

    @Override
    public CanalDto getCanalById(Long id) {
        Canal canal = canalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canal no encontrado"));
        return mapToDto(canal);
    }

    @Override
    @Transactional
    public CanalDto createCanal(CanalDto dto) {
        if (canalRepository.existsBySlug(dto.getSlug()))
            throw new RuntimeException("Slug ocupado");

        Canal canal = new Canal();
        TipoContenido tipo = tipoContenidoRepository.findByNombre("CANAL")
                .orElseThrow(() -> new RuntimeException("Tipo 'CANAL' no existe"));

        canal.setTipoContenido(tipo);
        mapDtoToEntity(dto, canal);
        return mapToDto(canalRepository.save(canal));
    }

    @Override
    @Transactional
    public CanalDto updateCanal(Long id, CanalDto dto) {
        Canal canal = canalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canal no encontrado"));

        if (!canal.getSlug().equals(dto.getSlug()) && canalRepository.existsBySlug(dto.getSlug())) {
            throw new RuntimeException("Slug ocupado");
        }
        mapDtoToEntity(dto, canal);
        return mapToDto(canalRepository.save(canal));
    }

    @Override
    public void deleteCanal(Long id) {
        Canal canal = canalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canal no encontrado"));

        canal.setEstaActivo(false);
        canalRepository.save(canal);
    }

    // Mappers
    private void mapDtoToEntity(CanalDto dto, Canal entity) {
        entity.setTitulo(dto.getTitulo());
        entity.setSlug(dto.getSlug());
        entity.setDescripcion(dto.getDescripcion());
        entity.setImgPoster(dto.getImgPortada());
        entity.setImgBanner(dto.getImgBanner());
        entity.setPrecio(dto.getPrecio());
        if (dto.getEstaActivo() != null)
            entity.setEstaActivo(dto.getEstaActivo());

        entity.setUrlStream(dto.getUrlStream());
        entity.setTipoTransmision(dto.getTipoTransmision());
        entity.setPais(dto.getPais());

        if (dto.getGenerosIds() != null) {
            List<Genero> generos = generoRepository.findAllById(dto.getGenerosIds());
            entity.setGeneros(new HashSet<>(generos));
        }
    }

    private CanalDto mapToDto(Canal entity) {
        List<GeneroDto> generosDto = entity.getGeneros().stream()
                .map(g -> new GeneroDto(g.getId(), g.getNombre()))
                .collect(Collectors.toList());

        return CanalDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .slug(entity.getSlug())
                .descripcion(entity.getDescripcion())
                .imgPortada(entity.getImgPoster())
                .imgBanner(entity.getImgBanner())
                .precio(entity.getPrecio())
                .estaActivo(entity.getEstaActivo())
                .urlStream(entity.getUrlStream())
                .tipoTransmision(entity.getTipoTransmision())
                .pais(entity.getPais())
                .generos(generosDto)
                .build();
    }
}
package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.EpisodioDto;
import com.MediaWeseco.Back.dtos.GeneroDto;
import com.MediaWeseco.Back.dtos.SerieDto;
import com.MediaWeseco.Back.dtos.TemporadaDto;
import com.MediaWeseco.Back.models.*;
import com.MediaWeseco.Back.repository.GeneroRepository;
import com.MediaWeseco.Back.repository.SerieRepository;
import com.MediaWeseco.Back.repository.TipoContenidoRepository;
import com.MediaWeseco.Back.service.SerieService;
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
public class SerieServiceImpl implements SerieService {

    private final SerieRepository serieRepository;
    private final TipoContenidoRepository tipoContenidoRepository;
    private final GeneroRepository generoRepository;

    @Override
    public Page<SerieDto> getAllSeries(Pageable pageable) {
        return serieRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<SerieDto> getActiveSeries(Pageable pageable) {
        return serieRepository.findByEstaActivoTrue(pageable).map(this::mapToDto);
    }

    @Override
    public SerieDto getSerieById(Long id) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        return mapToDto(serie);
    }

    @Override
    @Transactional // Importante: Maneja toda la operación como una sola transacción
    public SerieDto createSerie(SerieDto dto) {
        if (serieRepository.existsBySlug(dto.getSlug())) {
            throw new RuntimeException("El slug '" + dto.getSlug() + "' ya existe.");
        }

        Serie serie = new Serie();

        // 1. Asignar Tipo "SERIE"
        TipoContenido tipo = tipoContenidoRepository.findByNombre("SERIE")
                .orElseThrow(() -> new RuntimeException("Tipo 'SERIE' no encontrado en BD"));
        serie.setTipoContenido(tipo);

        // 2. Mapear todos los datos (incluyendo temporadas y episodios)
        mapDtoToEntity(dto, serie);

        // 3. Guardar (El CascadeType.ALL guardará temporadas y episodios)
        Serie guardada = serieRepository.save(serie);
        return mapToDto(guardada);
    }

    @Override
    @Transactional
    public SerieDto updateSerie(Long id, SerieDto dto) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        if (!serie.getSlug().equals(dto.getSlug()) && serieRepository.existsBySlug(dto.getSlug())) {
            throw new RuntimeException("El slug '" + dto.getSlug() + "' ya existe.");
        }

        // Al actualizar, reemplazamos los datos
        mapDtoToEntity(dto, serie);

        Serie actualizada = serieRepository.save(serie);
        return mapToDto(actualizada);
    }

    @Override
    public void deleteSerie(Long id) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        // CAMBIO: Soft Delete
        serie.setEstaActivo(false);
        serieRepository.save(serie);
    }

    private void mapDtoToEntity(SerieDto dto, Serie entity) {
        entity.setTitulo(dto.getTitulo());
        entity.setSlug(dto.getSlug());
        entity.setDescripcion(dto.getDescripcion());
        entity.setAnioLanzamiento(dto.getAnioLanzamiento());
        entity.setImgPoster(dto.getImgPortada());
        entity.setImgBanner(dto.getImgBanner());
        entity.setPrecio(dto.getPrecio());
        if (dto.getEstaActivo() != null)
            entity.setEstaActivo(dto.getEstaActivo());

        entity.setEstadoSerie(dto.getEstadoSerie());
        entity.setTotalTemporadas(dto.getTotalTemporadas());

        // ---Géneros ---
        if (dto.getGenerosIds() != null) {
            List<Genero> generos = generoRepository.findAllById(dto.getGenerosIds());
            entity.setGeneros(new HashSet<>(generos));
        }

        // --- TEMPORADAS Y EPISODIOS (Lógica "Merge" Inteligente) ---
        if (dto.getTemporadas() != null) {

            if (!dto.getTemporadas().isEmpty()) {
            }

            for (TemporadaDto tempDto : dto.getTemporadas()) {
                Temporada temporada = entity.getTemporadas().stream()
                        .filter(t -> t.getNumeroTemporada().equals(tempDto.getNumeroTemporada()))
                        .findFirst()
                        .orElse(null);

                if (temporada == null) {
                    // SI NO EXISTE: La creamos nueva
                    temporada = new Temporada();
                    temporada.setSerie(entity); // Vinculamos al padre
                    entity.getTemporadas().add(temporada);
                }

                // ACTUALIZAMOS DATOS (Sea nueva o vieja)
                temporada.setNumeroTemporada(tempDto.getNumeroTemporada());
                temporada.setTitulo(tempDto.getTitulo());
                temporada.setFechaEstreno(tempDto.getFechaEstreno());

                // --- LÓGICA ANIDADA PARA EPISODIOS ---
                if (tempDto.getEpisodios() != null) {
                    for (EpisodioDto epiDto : tempDto.getEpisodios()) {
                        // Buscamos si el episodio ya existe
                        Temporada finalTemporada = temporada; // Variable efectiva para lambda
                        Episodio episodio = temporada.getEpisodios().stream()
                                .filter(e -> e.getNumeroEpisodio().equals(epiDto.getNumeroEpisodio()))
                                .findFirst()
                                .orElse(null);

                        if (episodio == null) {
                            // Crear Nuevo
                            episodio = new Episodio();
                            episodio.setTemporada(temporada);
                            temporada.getEpisodios().add(episodio);
                        }

                        // Actualizar Datos
                        episodio.setNumeroEpisodio(epiDto.getNumeroEpisodio());
                        episodio.setTitulo(epiDto.getTitulo());
                        episodio.setDescripcion(epiDto.getDescripcion());
                        episodio.setDuracionMinutos(epiDto.getDuracionMinutos());
                        episodio.setUrlStream(epiDto.getUrlStream());
                        episodio.setImgMiniatura(epiDto.getImgMiniatura());
                    }
                }
            }
        }
    }

    private SerieDto mapToDto(Serie entity) {
        // Mapear Géneros
        List<GeneroDto> generosDto = entity.getGeneros().stream()
                .map(g -> new GeneroDto(g.getId(), g.getNombre()))
                .collect(Collectors.toList());

        // Mapear Temporadas (y sus episodios)
        List<TemporadaDto> temporadasDto = entity.getTemporadas().stream().map(t -> {

            List<EpisodioDto> episodiosDto = t.getEpisodios().stream().map(e -> EpisodioDto.builder()
                    .id(e.getId())
                    .numeroEpisodio(e.getNumeroEpisodio())
                    .titulo(e.getTitulo())
                    .descripcion(e.getDescripcion())
                    .duracionMinutos(e.getDuracionMinutos())
                    .urlStream(e.getUrlStream())
                    .imgMiniatura(e.getImgMiniatura())
                    .build()).collect(Collectors.toList());

            return TemporadaDto.builder()
                    .id(t.getId())
                    .numeroTemporada(t.getNumeroTemporada())
                    .titulo(t.getTitulo())
                    .fechaEstreno(t.getFechaEstreno())
                    .episodios(episodiosDto)
                    .build();
        }).collect(Collectors.toList());

        return SerieDto.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .slug(entity.getSlug())
                .descripcion(entity.getDescripcion())
                .anioLanzamiento(entity.getAnioLanzamiento())
                .imgPortada(entity.getImgPoster())
                .imgBanner(entity.getImgBanner())
                .precio(entity.getPrecio())
                .estaActivo(entity.getEstaActivo())
                .estadoSerie(entity.getEstadoSerie())
                .totalTemporadas(entity.getTotalTemporadas())
                .generos(generosDto)
                .temporadas(temporadasDto) // ¡Aquí va todo el árbol!
                .build();
    }
}
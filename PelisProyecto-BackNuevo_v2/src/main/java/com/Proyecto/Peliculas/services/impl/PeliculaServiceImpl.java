package com.Proyecto.Peliculas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaDetalleDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaRequestDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;
import com.Proyecto.Peliculas.dtos.generos.GeneroDTO;
import com.Proyecto.Peliculas.dtos.imagen.ImagenPeliculaDTO;
import com.Proyecto.Peliculas.exceptions.ResourceNotFoundException;
import com.Proyecto.Peliculas.models.Genero;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.repository.GeneroRepository;
import com.Proyecto.Peliculas.repository.PeliculaRepository;
import com.Proyecto.Peliculas.services.PeliculaService;

@Service
public class PeliculaServiceImpl implements PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    // --- MÉTODOS PÚBLICOS (Lectura) ---

    @Override
    @Transactional(readOnly = true) // Optimización: transacciones de solo lectura
    public Page<PeliculaResumenDTO> obtenerCatalogoPaginado(Pageable pageable) {
        // 1. Llama al repositorio (que devuelve Page<Pelicula>)
        Page<Pelicula> peliculasPage = peliculaRepository.findAll(pageable);

        // 2. Convierte la página de Entidades a una página de DTOs Resumen
        return peliculasPage.map(this::mapPeliculaToResumenDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PeliculaDetalleDTO obtenerPeliculaPorId(Long id) {
        // 1. Busca la película o lanza excepción 404
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", id));

        // 2. Convierte la Entidad a DTO de Detalle (pesado)
        return mapPeliculaToDetalleDTO(pelicula);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeliculaResumenDTO> buscarPeliculasPorTitulo(String titulo, Pageable pageable) {
        // 1. Usa el método personalizado del repositorio
        Page<Pelicula> peliculasPage = peliculaRepository.findByTituloContainingIgnoreCase(titulo, pageable);

        // 2. Convierte a DTO Resumen
        return peliculasPage.map(this::mapPeliculaToResumenDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeliculaResumenDTO> buscarPeliculasPorGenero(Long generoId, Pageable pageable) {
        // 1. Usa el método personalizado del repositorio
        Page<Pelicula> peliculasPage = peliculaRepository.findByGenerosId(generoId, pageable);

        // 2. Convierte a DTO Resumen
        return peliculasPage.map(this::mapPeliculaToResumenDTO);
    }

    // --- MÉTODOS DE ADMIN (CRUD) ---

    @Override
    @Transactional // Transacción de escritura (no es readOnly)
    public PeliculaDetalleDTO crearPelicula(PeliculaRequestDTO peliculaRequestDTO) {
        // 1. Crea la entidad Pelicula a partir del DTO
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(peliculaRequestDTO.getTitulo());
        pelicula.setDescripcion(peliculaRequestDTO.getDescripcion());
        pelicula.setAnioLanzamiento(peliculaRequestDTO.getAnioLanzamiento());
        pelicula.setDuracionMinutos(peliculaRequestDTO.getDuracionMinutos());
        pelicula.setDisponible(peliculaRequestDTO.getDisponible());
        pelicula.setPrecioComprar(peliculaRequestDTO.getPrecioComprar());
        pelicula.setImgFrente(peliculaRequestDTO.getImgFrente());
        pelicula.setUrlStream(peliculaRequestDTO.getUrlStream());

        // 2. Lógica de Géneros (La parte clave)
        if (peliculaRequestDTO.getGenerosId() != null && !peliculaRequestDTO.getGenerosId().isEmpty()) {
            // Busca todas las entidades Genero a partir de la lista de IDs
            List<Genero> generos = generoRepository.findAllById(peliculaRequestDTO.getGenerosId());
            // Las asigna a la película
            pelicula.setGeneros(generos);
        }

        // 3. Guarda la nueva película en la BD
        Pelicula peliculaGuardada = peliculaRepository.save(pelicula);

        // 4. Devuelve el DTO de Detalle
        return mapPeliculaToDetalleDTO(peliculaGuardada);
    }

    @Override
    @Transactional
    public PeliculaDetalleDTO actualizarPelicula(Long id, PeliculaRequestDTO peliculaRequestDTO) {
        // 1. Busca la película existente o lanza 404
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", id));

        // 2. Actualiza los campos de la entidad con los datos del DTO
        pelicula.setTitulo(peliculaRequestDTO.getTitulo());
        pelicula.setDescripcion(peliculaRequestDTO.getDescripcion());
        pelicula.setAnioLanzamiento(peliculaRequestDTO.getAnioLanzamiento());
        pelicula.setDuracionMinutos(peliculaRequestDTO.getDuracionMinutos());
        pelicula.setDisponible(peliculaRequestDTO.getDisponible());
        pelicula.setPrecioComprar(peliculaRequestDTO.getPrecioComprar());
        pelicula.setImgFrente(peliculaRequestDTO.getImgFrente());
        pelicula.setUrlStream(peliculaRequestDTO.getUrlStream());

        // 3. Lógica de Géneros (Actualización)
        // Limpia los géneros anteriores
        pelicula.getGeneros().clear();
        if (peliculaRequestDTO.getGenerosId() != null && !peliculaRequestDTO.getGenerosId().isEmpty()) {
            // Busca y asigna la nueva lista de géneros
            List<Genero> generos = generoRepository.findAllById(peliculaRequestDTO.getGenerosId());
            pelicula.setGeneros(generos);
        }

        // 4. Guarda la película actualizada
        Pelicula peliculaActualizada = peliculaRepository.save(pelicula);

        // 5. Devuelve el DTO de Detalle
        return mapPeliculaToDetalleDTO(peliculaActualizada);
    }

    @Override
    @Transactional
    public void eliminarPelicula(Long id) {
        // 1. Busca la película para asegurarse de que existe (y lanzar 404 si no)
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", id));

        // 2. Elimina la película
        peliculaRepository.delete(pelicula);
    }

    // --- MÉTODOS HELPER (Mapeadores) ---
    // Los creamos para no repetir código (Principio DRY)

    /**
     * Convierte una Entidad Pelicula a un DTO ligero (Resumen).
     */
    private PeliculaResumenDTO mapPeliculaToResumenDTO(Pelicula pelicula) {
        PeliculaResumenDTO dto = new PeliculaResumenDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setImgFrente(pelicula.getImgFrente());
        dto.setAnioLanzamiento(pelicula.getAnioLanzamiento());
        dto.setPrecioComprar(pelicula.getPrecioComprar());
        return dto;
    }

    /**
     * Convierte una Entidad Pelicula a un DTO pesado (Detalle).
     */
    private PeliculaDetalleDTO mapPeliculaToDetalleDTO(Pelicula pelicula) {
        PeliculaDetalleDTO dto = new PeliculaDetalleDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setDescripcion(pelicula.getDescripcion());
        dto.setAnioLanzamiento(pelicula.getAnioLanzamiento());
        dto.setDuracionMinutos(pelicula.getDuracionMinutos());
        dto.setPrecioComprar(pelicula.getPrecioComprar());
        dto.setUrlStream(pelicula.getUrlStream());

        // ← AGREGA ESTA LÍNEA AQUÍ
        dto.setImgFrente(pelicula.getImgFrente());

        // Mapea las listas de relaciones (usando sus propios DTOs)
        dto.setGeneros(pelicula.getGeneros().stream().map(genero -> {
            GeneroDTO generoDTO = new GeneroDTO();
            generoDTO.setId(genero.getId());
            generoDTO.setNombre(genero.getNombre());
            return generoDTO;
        }).collect(Collectors.toList()));

        dto.setImagenes(pelicula.getImagenes().stream().map(imagen -> {
            ImagenPeliculaDTO imgDTO = new ImagenPeliculaDTO();
            imgDTO.setId(imagen.getId());
            imgDTO.setUrlImagen(imagen.getUrlImagen());
            return imgDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
}
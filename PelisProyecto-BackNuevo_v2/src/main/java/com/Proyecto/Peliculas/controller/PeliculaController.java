package com.Proyecto.Peliculas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaDetalleDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaRequestDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;
import com.Proyecto.Peliculas.services.PeliculaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/peliculas")
// NOTA: No necesitamos @CrossOrigin aquí porque ya lo configuramos
// globalmente en WebConfig.java.
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    // --- ENDPOINTS PÚBLICOS (GET) ---
    // (Permitidos para todos en SecurityConfig)

    /**
     * GET /api/peliculas
     * Obtiene el catálogo paginado.
     * Ejemplo: /api/peliculas?page=0&size=10&sort=titulo,asc
     */
    @GetMapping
    public ResponseEntity<Page<PeliculaResumenDTO>> obtenerCatalogo(
            // @PageableDefault: Define valores por defecto si el front-end no los envía
            @PageableDefault(size = 10, sort = "anioLanzamiento") Pageable pageable) {
        
        Page<PeliculaResumenDTO> catalogo = peliculaService.obtenerCatalogoPaginado(pageable);
        return new ResponseEntity<>(catalogo, HttpStatus.OK);
    }

    /**
     * GET /api/peliculas/{id}
     * Obtiene el detalle de una sola película.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PeliculaDetalleDTO> obtenerDetallePelicula(@PathVariable Long id) {
        PeliculaDetalleDTO pelicula = peliculaService.obtenerPeliculaPorId(id);
        return new ResponseEntity<>(pelicula, HttpStatus.OK);
    }

    /**
     * GET /api/peliculas/buscar/titulo?q=...
     * Busca películas por título.
     */
    @GetMapping("/buscar/titulo")
    public ResponseEntity<Page<PeliculaResumenDTO>> buscarPorTitulo(
            @RequestParam("q") String titulo, // "q" de "query" (consulta)
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<PeliculaResumenDTO> resultado = peliculaService.buscarPeliculasPorTitulo(titulo, pageable);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    /**
     * GET /api/peliculas/buscar/genero?id=...
     * Busca películas por ID de género.
     */
    @GetMapping("/buscar/genero")
    public ResponseEntity<Page<PeliculaResumenDTO>> buscarPorGenero(
            @RequestParam("id") Long generoId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<PeliculaResumenDTO> resultado = peliculaService.buscarPeliculasPorGenero(generoId, pageable);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    // --- ENDPOINTS DE ADMIN (POST, PUT, DELETE) ---
    // (Protegidos por SecurityConfig para requerir ROLE_ADMIN)

    /**
     * POST /api/peliculas
     * Crea una nueva película. (Solo Admin)
     */
    @PostMapping
    public ResponseEntity<PeliculaDetalleDTO> crearPelicula(@Valid @RequestBody PeliculaRequestDTO peliculaDTO) {
        PeliculaDetalleDTO peliculaNueva = peliculaService.crearPelicula(peliculaDTO);
        // Devolvemos 201 Created y los detalles de la película creada
        return new ResponseEntity<>(peliculaNueva, HttpStatus.CREATED);
    }

    /**
     * PUT /api/peliculas/{id}
     * Actualiza una película existente. (Solo Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<PeliculaDetalleDTO> actualizarPelicula(
            @PathVariable Long id,
            @Valid @RequestBody PeliculaRequestDTO peliculaDTO) {
        
        PeliculaDetalleDTO peliculaActualizada = peliculaService.actualizarPelicula(id, peliculaDTO);
        return new ResponseEntity<>(peliculaActualizada, HttpStatus.OK);
    }

    /**
     * DELETE /api/peliculas/{id}
     * Elimina una película. (Solo Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPelicula(@PathVariable Long id) {
        peliculaService.eliminarPelicula(id);
        // Devolvemos 204 No Content (éxito, pero sin cuerpo de respuesta)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
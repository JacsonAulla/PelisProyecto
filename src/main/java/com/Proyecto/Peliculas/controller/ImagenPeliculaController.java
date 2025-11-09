package com.Proyecto.Peliculas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.imagen.ImagenCrearDTO;
import com.Proyecto.Peliculas.models.ImagenPelicula;
import com.Proyecto.Peliculas.services.ImagenPeliculaService;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenPeliculaController {
    
    @Autowired
    private ImagenPeliculaService imagenPeliculaService;

    // Obtener todas las imágenes
    @GetMapping
    public List<ImagenPelicula> listarTodas() {
        return imagenPeliculaService.listarTodas();
    }

    // Obtener imágenes de una película específica
    @GetMapping("/pelicula/{peliculaId}")
    public List<ImagenPelicula> listarPorPelicula(@PathVariable Long peliculaId) {
        return imagenPeliculaService.listarPorPelicula(peliculaId);
    }

    // Crear imagen
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ImagenCrearDTO dto) {
        try {
            // Validar que se proporcionó la URL
            if (dto.getUrlImagen() == null || dto.getUrlImagen().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("La URL de la imagen es requerida");
            }
            
            // Validar que se proporcionó el ID de película
            if (dto.getPeliculaId() == null) {
                return ResponseEntity.badRequest().body("El ID de la película es requerido");
            }
            
            // Crear la imagen (el service hace la validación de que la película existe)
            ImagenPelicula nueva = imagenPeliculaService.crearImagen(dto.getUrlImagen(), dto.getPeliculaId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar imagen
    @PutMapping("/{id}")
    public ResponseEntity<ImagenPelicula> actualizar(@PathVariable Long id, @RequestBody ImagenPelicula datosActualizados) {
        ImagenPelicula actualizada = imagenPeliculaService.actualizarImagen(id, datosActualizados);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar imagen por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        imagenPeliculaService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar todas las imágenes de una película
    @DeleteMapping("/pelicula/{peliculaId}")
    public ResponseEntity<Void> eliminarPorPelicula(@PathVariable Long peliculaId) {
        imagenPeliculaService.eliminarPorPelicula(peliculaId);
        return ResponseEntity.noContent().build();
    }
}

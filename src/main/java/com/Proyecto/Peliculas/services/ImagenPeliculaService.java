package com.Proyecto.Peliculas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.models.ImagenPelicula;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.repository.ImagenPeliculaRepository;
import com.Proyecto.Peliculas.repository.PeliculaRepository;

@Service
public class ImagenPeliculaService {
    
    @Autowired
    private ImagenPeliculaRepository imagenPeliculaRepository;
    
    @Autowired
    private PeliculaRepository peliculaRepository;

    // Obtener todas las imágenes
    @Transactional(readOnly = true)
    public List<ImagenPelicula> listarTodas() {
        return imagenPeliculaRepository.findAll();
    }

    // Obtener imágenes de una película específica
    @Transactional(readOnly = true)
    public List<ImagenPelicula> listarPorPelicula(Long peliculaId) {
        return imagenPeliculaRepository.findByPeliculaId(peliculaId);
    }

    // Crear nueva imagen con validaciones
    public ImagenPelicula crearImagen(String urlImagen, Long peliculaId) {
        // Validar URL
        if (urlImagen == null || urlImagen.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen no puede estar vacía");
        }
        
        // Buscar y validar que la película existe
        Optional<Pelicula> peliculaOptional = peliculaRepository.findById(peliculaId);
        if (peliculaOptional.isEmpty()) {
            throw new IllegalArgumentException("La película con ID " + peliculaId + " no existe");
        }
        
        // Crear y guardar la imagen
        ImagenPelicula imagen = new ImagenPelicula();
        imagen.setUrlImagen(urlImagen);
        imagen.setPelicula(peliculaOptional.get());
        
        return imagenPeliculaRepository.save(imagen);
    }

    // Actualizar imagen existente
    public ImagenPelicula actualizarImagen(Long id, ImagenPelicula datosActualizados) {
        Optional<ImagenPelicula> existente = imagenPeliculaRepository.findById(id);
        
        if (existente.isPresent()) {
            ImagenPelicula imagen = existente.get();
            
            // Solo actualizar si la nueva URL no está vacía
            if (datosActualizados.getUrlImagen() != null && !datosActualizados.getUrlImagen().trim().isEmpty()) {
                imagen.setUrlImagen(datosActualizados.getUrlImagen());
            }
            
            return imagenPeliculaRepository.save(imagen);
        }
        
        return null;
    }

    // Eliminar imagen por ID
    public void eliminarPorId(Long id) {
        imagenPeliculaRepository.deleteById(id);
    }

    // Eliminar todas las imágenes de una película
    @Transactional
    public void eliminarPorPelicula(Long peliculaId) {
        imagenPeliculaRepository.deleteByPeliculaId(peliculaId);
    }
}

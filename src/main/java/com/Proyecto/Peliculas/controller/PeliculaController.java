package com.Proyecto.Peliculas.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaAdminDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaCrearDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaUsuarioDTO;
import com.Proyecto.Peliculas.models.Genero;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.repository.GeneroRepository;
import com.Proyecto.Peliculas.services.PeliculaService;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private GeneroRepository generoRepository;

    //para el usuario
    
    // Obtener todas las películas (vista usuario)
    @GetMapping("/usuario")
    public List<PeliculaUsuarioDTO> getAllForUser() {
        return peliculaService.obtenerTodasParaUsuario();
    }

    // Buscar película por título (usuario)
    @GetMapping("/usuario/buscar")
    public ResponseEntity<PeliculaUsuarioDTO> getByTituloForUser(@RequestParam String titulo) {
        Optional<PeliculaUsuarioDTO> dto = peliculaService.obtenerPorTituloParaUsuario(titulo);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //para admin
    
    // Obtener todas las películas (admin)
    @GetMapping("/admin")
    public List<PeliculaAdminDTO> getAllForAdmin() {
        return peliculaService.obtenerTodasParaAdmin();
    }

    // Obtener todas las disponibles
    @GetMapping("/admin/disponibles")
    public List<PeliculaAdminDTO> getDisponiblesForAdmin() {
        return peliculaService.obtenerDisponibles();
    }

    // Obtener película por id (admin)
    @GetMapping("/admin/{id}")
    public ResponseEntity<PeliculaAdminDTO> getByIdForAdmin(@PathVariable Long id) {
        Optional<PeliculaAdminDTO> dto = peliculaService.obtenerPorIdAdmin(id);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear película (admin)
    @PostMapping("/admin")
    public ResponseEntity<PeliculaAdminDTO> crearPelicula(@RequestBody PeliculaCrearDTO dto) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(dto.getTitulo());
        pelicula.setDescripcion(dto.getDescripcion());
        pelicula.setAnioLanzamiento(dto.getAnioLanzamiento());
        pelicula.setDuracionMinutos(dto.getDuracionMinutos());
        pelicula.setDisponible(dto.getDisponible());
        pelicula.setPrecioComprar(dto.getPrecioComprar());
        pelicula.setImgFrente(dto.getImgFrente());
        pelicula.setUrlStream(dto.getUrlStream());
        
        // Obtener géneros por IDs
        List<Genero> generos = generoRepository.findAllById(dto.getGenerosIds());
        pelicula.setGeneros(generos);
        
        Pelicula nueva = peliculaService.crearPelicula(pelicula);
        
        // Retornar DTO convertido usando el método del service
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(peliculaService.convertirAPeliculaAdminDTO(nueva));
    }

    // Actualizar película (admin)
    @PutMapping("/admin/{id}")
    public ResponseEntity<PeliculaAdminDTO> actualizarPelicula(@PathVariable Long id, @RequestBody Pelicula pelicula) {
        Pelicula actualizada = peliculaService.actualizarPelicula(id, pelicula);
        if (actualizada != null) {
            // Retornar DTO convertido
            return ResponseEntity.ok(peliculaService.convertirAPeliculaAdminDTO(actualizada));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar película (admin)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        peliculaService.eliminarPelicula(id);
        return ResponseEntity.noContent().build();
    }
}

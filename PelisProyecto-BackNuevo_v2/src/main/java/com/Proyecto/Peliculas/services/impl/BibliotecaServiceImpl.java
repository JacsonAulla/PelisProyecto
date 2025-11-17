package com.Proyecto.Peliculas.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;
import com.Proyecto.Peliculas.dtos.biblioteca.BibliotecaResponseDTO;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.models.Usuario;
import com.Proyecto.Peliculas.models.UsuarioPelicula;
import com.Proyecto.Peliculas.repository.UsuarioPeliculaRepository;
import com.Proyecto.Peliculas.repository.UsuarioRepository;
import com.Proyecto.Peliculas.services.BibliotecaService;

@Service
public class BibliotecaServiceImpl implements BibliotecaService {

    @Autowired private UsuarioPeliculaRepository usuarioPeliculaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BibliotecaResponseDTO> obtenerMiBiblioteca(Pageable pageable) {
        // 1. Obtener al usuario logueado
        Usuario usuario = obtenerUsuarioLogueado();
        
        // 2. Buscar su biblioteca en la BD
        Page<UsuarioPelicula> bibliotecaPage = usuarioPeliculaRepository.findByUsuarioId(usuario.getId(), pageable);
        
        // 3. Mapear la página a DTOs de respuesta
        return bibliotecaPage.map(this::mapBibliotecaToResponseDTO);
    }
    
    // --- MÉTODOS HELPER (Privados) ---

    private Usuario obtenerUsuarioLogueado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = authentication.getName();
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el token: " + emailUsuario));
    }
    
    private BibliotecaResponseDTO mapBibliotecaToResponseDTO(UsuarioPelicula item) {
        BibliotecaResponseDTO dto = new BibliotecaResponseDTO();
        dto.setIdBiblioteca(item.getId());
        dto.setFechaCompra(item.getFechaCompra());
        
        // Mapea la película asociada
        dto.setPelicula(mapPeliculaToResumenDTO(item.getPelicula()));
        return dto;
    }
    
    private PeliculaResumenDTO mapPeliculaToResumenDTO(Pelicula pelicula) {
        PeliculaResumenDTO dto = new PeliculaResumenDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setImgFrente(pelicula.getImgFrente());
        dto.setAnioLanzamiento(pelicula.getAnioLanzamiento());
        dto.setPrecioComprar(pelicula.getPrecioComprar());
        return dto;
    }
}
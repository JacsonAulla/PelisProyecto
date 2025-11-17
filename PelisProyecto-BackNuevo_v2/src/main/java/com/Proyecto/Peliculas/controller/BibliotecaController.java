package com.Proyecto.Peliculas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.biblioteca.BibliotecaResponseDTO;
import com.Proyecto.Peliculas.services.BibliotecaService;

@RestController
@RequestMapping("/api/biblioteca")
// (Configuración CORS global ya aplicada en WebConfig)
public class BibliotecaController {

    @Autowired
    private BibliotecaService bibliotecaService;

    // --- ENDPOINT PARA EL USUARIO LOGUEADO ---
    // (Protegido por SecurityConfig para requerir @Authenticated)

    /**
     * GET /api/biblioteca/mi-biblioteca
     * Obtiene la biblioteca de películas compradas por el usuario logueado.
     */
    @GetMapping("/mi-biblioteca")
    public ResponseEntity<Page<BibliotecaResponseDTO>> obtenerMiBiblioteca(
            @PageableDefault(size = 20, sort = "fechaCompra") Pageable pageable) {
        
        // El servicio buscará al usuario desde el token JWT
        Page<BibliotecaResponseDTO> biblioteca = bibliotecaService.obtenerMiBiblioteca(pageable);
        return new ResponseEntity<>(biblioteca, HttpStatus.OK);
    }
}
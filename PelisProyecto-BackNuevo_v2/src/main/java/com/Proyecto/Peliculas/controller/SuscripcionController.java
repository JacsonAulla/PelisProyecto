package com.Proyecto.Peliculas.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionRequestDTO;
import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionResponseDTO;
import com.Proyecto.Peliculas.services.SuscripcionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suscripciones")
// (Configuración CORS global ya aplicada en WebConfig)
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    // --- ENDPOINTS PARA EL USUARIO LOGUEADO ---
    // (Protegidos por SecurityConfig para requerir @Authenticated)

    /**
     * GET /api/suscripciones/mi-estado
     * Obtiene el estado de la suscripción actual o más reciente
     * del usuario que está logueado (dueño del token).
     */
    @GetMapping("/mi-estado")
    public ResponseEntity<SuscripcionResponseDTO> obtenerMiEstado() {
        // El servicio buscará al usuario desde el token JWT
        SuscripcionResponseDTO estado = suscripcionService.obtenerMiEstadoSuscripcion();
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }

    /**
     * POST /api/suscripciones/crear
     * Crea una nueva suscripción (ej. de 1 mes) para el usuario logueado.
     */
    @PostMapping("/crear")
    public ResponseEntity<SuscripcionResponseDTO> crearSuscripcion(
            @Valid @RequestBody SuscripcionRequestDTO requestDTO) {
        
        // El servicio asignará esta suscripción al usuario del token
        SuscripcionResponseDTO nuevaSuscripcion = suscripcionService.crearSuscripcion(requestDTO);
        return new ResponseEntity<>(nuevaSuscripcion, HttpStatus.CREATED);
    }

    /**
     * POST /api/suscripciones/cancelar
     * Cancela la suscripción ACTIVA del usuario logueado.
     */
    @PostMapping("/cancelar")
    public ResponseEntity<SuscripcionResponseDTO> cancelarSuscripcion() {
        // El servicio buscará la suscripción activa del usuario del token
        SuscripcionResponseDTO suscripcionCancelada = suscripcionService.cancelarSuscripcion();
        return new ResponseEntity<>(suscripcionCancelada, HttpStatus.OK);
    }

    // --- ENDPOINT DE ADMIN ---
    // (Protegido por SecurityConfig para requerir ROLE_ADMIN)

    /**
     * GET /api/suscripciones/admin/todas
     * Obtiene una lista paginada de TODAS las suscripciones de TODOS los usuarios.
     * (Solo Admin)
     */
    @GetMapping("/admin/todas")
    public ResponseEntity<Page<SuscripcionResponseDTO>> obtenerTodas(
            @PageableDefault(size = 20, sort = "fechaInicio") Pageable pageable) {
        
        Page<SuscripcionResponseDTO> pagina = suscripcionService.obtenerTodasLasSuscripciones(pageable);
        return new ResponseEntity<>(pagina, HttpStatus.OK);
    }
}
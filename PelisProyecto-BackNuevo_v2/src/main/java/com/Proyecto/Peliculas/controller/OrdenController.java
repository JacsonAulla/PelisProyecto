package com.Proyecto.Peliculas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyecto.Peliculas.dtos.compra.OrdenRequestDTO;
import com.Proyecto.Peliculas.dtos.compra.OrdenResponseDTO;
import com.Proyecto.Peliculas.services.OrdenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes")
// (Configuración CORS global ya aplicada en WebConfig)
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // --- ENDPOINTS PARA EL USUARIO LOGUEADO ---
    // (Protegidos por SecurityConfig para requerir @Authenticated)

    /**
     * POST /api/ordenes/crear
     * Crea una nueva orden de compra (procesa el carrito).
     */
    @PostMapping("/crear")
    public ResponseEntity<OrdenResponseDTO> crearOrden(
            @Valid @RequestBody OrdenRequestDTO ordenRequestDTO) {
        
        // El servicio procesará el pago (simulado), creará la orden,
        // y añadirá las películas a la biblioteca del usuario logueado.
        OrdenResponseDTO nuevaOrden = ordenService.crearOrden(ordenRequestDTO);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    /**
     * GET /api/ordenes/mis-ordenes
     * Obtiene el historial de órdenes del usuario logueado.
     */
    @GetMapping("/mis-ordenes")
    public ResponseEntity<Page<OrdenResponseDTO>> obtenerMisOrdenes(
            @PageableDefault(size = 10, sort = "fechaCompra") Pageable pageable) {
        
        Page<OrdenResponseDTO> historial = ordenService.obtenerMisOrdenes(pageable);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }

    /**
     * GET /api/ordenes/{id}
     * Obtiene el detalle de UNA orden específica del usuario logueado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerOrdenPorId(@PathVariable Long id) {
        // El servicio validará que esta orden pertenezca al usuario logueado.
        OrdenResponseDTO orden = ordenService.obtenerOrdenPorId(id);
        return new ResponseEntity<>(orden, HttpStatus.OK);
    }

    // --- ENDPOINT DE ADMIN ---
    // (Protegido por SecurityConfig para requerir ROLE_ADMIN)

    /**
     * GET /api/ordenes/admin/todas
     * Obtiene una lista paginada de TODAS las órdenes de TODOS los usuarios.
     * (Solo Admin)
     */
    @GetMapping("/admin/todas")
    public ResponseEntity<Page<OrdenResponseDTO>> obtenerTodas(
            @PageableDefault(size = 20, sort = "fechaCompra") Pageable pageable) {
        
        Page<OrdenResponseDTO> pagina = ordenService.obtenerTodasLasOrdenes(pageable);
        return new ResponseEntity<>(pagina, HttpStatus.OK);
    }
}
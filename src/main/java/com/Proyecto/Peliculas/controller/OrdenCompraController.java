package com.Proyecto.Peliculas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.Proyecto.Peliculas.dtos.compra.OrdenCompraDTO;
import com.Proyecto.Peliculas.enums.EstadoOrden;
import com.Proyecto.Peliculas.models.DetalleOrden;
import com.Proyecto.Peliculas.models.OrdenCompra;
import com.Proyecto.Peliculas.services.OrdenCompraService;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    // ========== ENDPOINTS PARA USUARIO ==========

    // Obtener todas mis órdenes (el usuario logueado)
    @GetMapping("/usuario/{usuarioId}")
    public List<OrdenCompraDTO> obtenerMisOrdenes(@PathVariable Long usuarioId) {
        return ordenCompraService.obtenerOrdenesUsuario(usuarioId).stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    // Obtener mis órdenes completadas
    @GetMapping("/usuario/{usuarioId}/completadas")
    public List<OrdenCompraDTO> obtenerMisOrdenesCompletadas(@PathVariable Long usuarioId) {
        return ordenCompraService.obtenerOrdenesCompletadasUsuario(usuarioId).stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    // Obtener mis órdenes pendientes
    @GetMapping("/usuario/{usuarioId}/pendientes")
    public List<OrdenCompraDTO> obtenerMisOrdenesPendientes(@PathVariable Long usuarioId) {
        return ordenCompraService.obtenerOrdenesPendientesUsuario(usuarioId).stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    // Obtener detalles de una orden (qué películas compré)
    @GetMapping("/{ordenId}/detalles")
    public List<DetalleOrden> obtenerDetallesOrden(@PathVariable Long ordenId) {
        return ordenCompraService.obtenerDetallesOrden(ordenId);
    }

    // Obtener una orden completa por ID
    @GetMapping("/{ordenId}")
    public ResponseEntity<OrdenCompraDTO> obtenerOrdenPorId(@PathVariable Long ordenId) {
        Optional<OrdenCompra> orden = ordenCompraService.obtenerOrdenPorId(ordenId);
        return orden.map(o -> ResponseEntity.ok(ordenCompraService.convertirADTO(o)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ========== ENDPOINT PRINCIPAL: REALIZAR COMPRA ==========

    @PostMapping("/comprar/{usuarioId}")
    public ResponseEntity<?> realizarCompra(@PathVariable Long usuarioId, @RequestBody List<DetalleOrden> detalles) {
        try {
            if (detalles == null || detalles.isEmpty()) {
                return ResponseEntity.badRequest().body("Debes enviar al menos una película para comprar");
            }
            OrdenCompra nuevaOrden = ordenCompraService.crearOrdenConDetalles(usuarioId, detalles);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordenCompraService.convertirADTO(nuevaOrden));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la orden: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS PARA ADMIN ==========

    @GetMapping("/admin/todas")
    public List<OrdenCompraDTO> obtenerTodasLasOrdenes() {
        return ordenCompraService.obtenerTodasLasOrdenes().stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/admin/estado/{estado}")
    public List<OrdenCompraDTO> obtenerOrdenesPorEstado(@PathVariable EstadoOrden estado) {
        return ordenCompraService.obtenerOrdenesPorEstado(estado).stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/admin/fecha")
    public List<OrdenCompraDTO> obtenerOrdenesPorFecha(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        return ordenCompraService.obtenerOrdenesPorFecha(inicio, fin).stream()
            .map(ordenCompraService::convertirADTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/admin/pelicula/{peliculaId}/compras")
    public List<DetalleOrden> obtenerDetallesPorPelicula(@PathVariable Long peliculaId) {
        return ordenCompraService.obtenerDetallesPorPelicula(peliculaId);
    }

    // ========== ENDPOINTS CRUD PARA ADMIN ==========

    @PutMapping("/admin/{ordenId}/estado")
    public ResponseEntity<OrdenCompraDTO> actualizarEstadoOrden(
            @PathVariable Long ordenId,
            @RequestBody EstadoOrden nuevoEstado) {
        OrdenCompra actualizada = ordenCompraService.actualizarEstadoOrden(ordenId, nuevoEstado);
        if (actualizada != null) {
            return ResponseEntity.ok(ordenCompraService.convertirADTO(actualizada));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/admin/{ordenId}/total")
    public ResponseEntity<OrdenCompraDTO> actualizarTotalOrden(
            @PathVariable Long ordenId,
            @RequestBody BigDecimal nuevoTotal) {
        OrdenCompra actualizada = ordenCompraService.actualizarTotalOrden(ordenId, nuevoTotal);
        if (actualizada != null) {
            return ResponseEntity.ok(ordenCompraService.convertirADTO(actualizada));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/{ordenId}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long ordenId) {
        ordenCompraService.eliminarOrden(ordenId);
        return ResponseEntity.noContent().build();
    }

    // ========== ENDPOINTS AUXILIARES PARA ESTADÍSTICAS ==========

    @GetMapping("/usuario/{usuarioId}/estadisticas/completadas-count")
    public ResponseEntity<Long> contarOrdenesCompletadas(@PathVariable Long usuarioId) {
        Long count = ordenCompraService.contarOrdenesCompletadasUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/usuario/{usuarioId}/estadisticas/total-gastado")
    public ResponseEntity<BigDecimal> obtenerTotalGastado(@PathVariable Long usuarioId) {
        BigDecimal total = ordenCompraService.obtenerTotalGastadoUsuario(usuarioId);
        return ResponseEntity.ok(total);
    }
}

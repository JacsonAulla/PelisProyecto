package com.Proyecto.Peliculas.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.compra.DetalleOrdenDTO;
import com.Proyecto.Peliculas.dtos.compra.OrdenCompraDTO;
import com.Proyecto.Peliculas.enums.EstadoOrden;
import com.Proyecto.Peliculas.models.DetalleOrden;
import com.Proyecto.Peliculas.models.OrdenCompra;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.repository.DetalleOrdenRepository;
import com.Proyecto.Peliculas.repository.OrdenCompraRepository;
import com.Proyecto.Peliculas.repository.PeliculaRepository;

@Service
@Transactional
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerOrdenesUsuario(Long usuarioId) {
        return ordenCompraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerOrdenesCompletadasUsuario(Long usuarioId) {
        return ordenCompraRepository.findByUsuarioIdAndEstado(usuarioId, EstadoOrden.COMPLETADA);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerOrdenesPendientesUsuario(Long usuarioId) {
        return ordenCompraRepository.findByUsuarioIdAndEstadoOrderByFechaCompraDesc(usuarioId, EstadoOrden.PENDIENTE);
    }

    @Transactional(readOnly = true)
    public List<DetalleOrden> obtenerDetallesOrden(Long ordenId) {
        return detalleOrdenRepository.findByOrdenIdWithPelicula(ordenId);
    }

    @Transactional(readOnly = true)
    public Optional<OrdenCompra> obtenerOrdenPorId(Long ordenId) {
        return ordenCompraRepository.findById(ordenId);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerTodasLasOrdenes() {
        return ordenCompraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerOrdenesPorEstado(EstadoOrden estado) {
        return ordenCompraRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerOrdenesPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return ordenCompraRepository.findByFechaCompraBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<DetalleOrden> obtenerDetallesPorPelicula(Long peliculaId) {
        return detalleOrdenRepository.findByPeliculaId(peliculaId);
    }

    public OrdenCompra crearOrdenConDetalles(Long usuarioId, List<DetalleOrden> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un detalle (película)");
        }

        // 1. Crear la orden base sin detalles
        OrdenCompra orden = new OrdenCompra();
        orden.setUsuarioId(usuarioId);
        orden.setFechaCompra(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PENDIENTE);

        // 2. Cargar películas completas para cada detalle
        for (DetalleOrden detalle : detalles) {
            if (detalle.getPelicula() == null || detalle.getPelicula().getId() == null) {
                throw new IllegalArgumentException("Cada detalle debe contener una película válida");
            }
            Optional<Pelicula> peliculaOpt = peliculaRepository.findById(detalle.getPelicula().getId());
            if (peliculaOpt.isEmpty()) {
                throw new IllegalArgumentException("Película con ID " + detalle.getPelicula().getId() + " no existe.");
            }
            // Establecer película completa en detalle
            detalle.setPelicula(peliculaOpt.get());
        }

        // 3. Calcular total
        BigDecimal total = detalles.stream()
            .map(DetalleOrden::getPrecio)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setTotalPagado(total);

        // 4. Guardar la orden para tener ID
        orden = ordenCompraRepository.save(orden);

        // 5. Asociar orden y guardar detalles
        for (DetalleOrden detalle : detalles) {
            detalle.setOrden(orden);
        }
        detalleOrdenRepository.saveAll(detalles);

        // 6. Asignar detalles y retornar orden
        orden.setDetalles(detalles);
        return orden;
    }

    public OrdenCompra actualizarEstadoOrden(Long id, EstadoOrden nuevoEstado) {
        Optional<OrdenCompra> ordenOptional = ordenCompraRepository.findById(id);
        if (ordenOptional.isPresent()) {
            OrdenCompra orden = ordenOptional.get();
            orden.setEstado(nuevoEstado);
            return ordenCompraRepository.save(orden);
        }
        return null;
    }

    public OrdenCompra actualizarTotalOrden(Long id, BigDecimal nuevoTotal) {
        Optional<OrdenCompra> ordenOptional = ordenCompraRepository.findById(id);
        if (ordenOptional.isPresent()) {
            OrdenCompra orden = ordenOptional.get();
            if (nuevoTotal != null && nuevoTotal.compareTo(BigDecimal.ZERO) >= 0) {
                orden.setTotalPagado(nuevoTotal);
                return ordenCompraRepository.save(orden);
            }
        }
        return null;
    }

    public void eliminarOrden(Long id) {
        if (ordenCompraRepository.existsById(id)) {
            ordenCompraRepository.deleteById(id);
        }
    }

    public void eliminarOrdenesUsuario(Long usuarioId) {
        List<OrdenCompra> ordenes = ordenCompraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
        for (OrdenCompra orden : ordenes) {
            ordenCompraRepository.delete(orden);
        }
    }

    @Transactional(readOnly = true)
    public Long contarOrdenesCompletadasUsuario(Long usuarioId) {
        return ordenCompraRepository.findByUsuarioIdAndEstado(usuarioId, EstadoOrden.COMPLETADA).stream().count();
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalGastadoUsuario(Long usuarioId) {
        List<OrdenCompra> ordenes = ordenCompraRepository.findByUsuarioIdAndEstado(usuarioId, EstadoOrden.COMPLETADA);
        return ordenes.stream()
            .map(OrdenCompra::getTotalPagado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Conversión a DTO
    public OrdenCompraDTO convertirADTO(OrdenCompra orden) {
        OrdenCompraDTO dto = new OrdenCompraDTO();
        dto.setId(orden.getId());
        dto.setUsuarioId(orden.getUsuarioId());
        dto.setEstado(orden.getEstado());
        dto.setFechaCompra(orden.getFechaCompra());
        dto.setTotalPagado(orden.getTotalPagado());

        List<DetalleOrdenDTO> detallesDTO = orden.getDetalles().stream()
            .map(d -> {
                String titulo = null;
                if (d.getPelicula() != null) {
                    // Intentar obtener el título, sino poner valor por defecto
                    titulo = d.getPelicula().getTitulo() != null ? d.getPelicula().getTitulo() : "Título no disponible";
                }
                return new DetalleOrdenDTO(
                    d.getId(),
                    d.getPelicula() != null ? d.getPelicula().getId() : null,
                    titulo,
                    d.getPrecio()
                );
            }).collect(Collectors.toList());

        dto.setDetalles(detallesDTO);
        return dto;
    }
}

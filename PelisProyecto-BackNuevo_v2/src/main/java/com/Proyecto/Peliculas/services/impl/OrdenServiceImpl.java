package com.Proyecto.Peliculas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;
import com.Proyecto.Peliculas.dtos.compra.DetalleOrdenResponseDTO;
import com.Proyecto.Peliculas.dtos.compra.OrdenRequestDTO;
import com.Proyecto.Peliculas.dtos.compra.OrdenResponseDTO;
import com.Proyecto.Peliculas.enums.EstadoOrden;
import com.Proyecto.Peliculas.exceptions.ResourceNotFoundException;
import com.Proyecto.Peliculas.models.DetalleOrden;
import com.Proyecto.Peliculas.models.OrdenCompra;
import com.Proyecto.Peliculas.models.Pelicula;
import com.Proyecto.Peliculas.models.Usuario;
import com.Proyecto.Peliculas.models.UsuarioPelicula;
import com.Proyecto.Peliculas.repository.OrdenCompraRepository;
import com.Proyecto.Peliculas.repository.PeliculaRepository;
import com.Proyecto.Peliculas.repository.UsuarioPeliculaRepository;
import com.Proyecto.Peliculas.repository.UsuarioRepository;
import com.Proyecto.Peliculas.services.OrdenService;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private PeliculaRepository peliculaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioPeliculaRepository usuarioPeliculaRepository;

    // --- MÉTODOS DE USUARIO ---

    @Override
    @Transactional // ¡Transacción VITAL! Si algo falla, revierte todo (la orden y la biblioteca)
    public OrdenResponseDTO crearOrden(OrdenRequestDTO ordenRequestDTO) {

        // 1. Obtener al usuario logueado
        Usuario usuario = obtenerUsuarioLogueado();

        // 2. Validar el carrito
        if (ordenRequestDTO.getPeliculasId() == null || ordenRequestDTO.getPeliculasId().isEmpty()) {
            throw new RuntimeException("El carrito no puede estar vacío.");
        }

        // 3. Obtener las entidades Pelicula del carrito
        List<Pelicula> peliculasEnCarrito = peliculaRepository.findAllById(ordenRequestDTO.getPeliculasId());

        // 4. Validar que todas las películas solicitadas existan
        if (peliculasEnCarrito.size() != ordenRequestDTO.getPeliculasId().size()) {
            throw new RuntimeException("Algunas películas en el carrito no existen.");
        }

        // 5. Validar Regla de Negocio: El usuario no puede comprar una película que ya
        // posee
        for (Pelicula p : peliculasEnCarrito) {
            if (usuarioPeliculaRepository.existsByUsuarioIdAndPeliculaId(usuario.getId(), p.getId())) {
                throw new RuntimeException("Ya posees la película: " + p.getTitulo());
            }
        }

        // --- 6. (Lógica de Pago Simulado) ---
        // Aquí procesaríamos el 'paymentMethodToken' con Stripe/MercadoPago.
        // Asumimos que el pago es exitoso.

        // 7. Calcular el total y crear los detalles
        BigDecimal totalPagado = BigDecimal.ZERO;
        List<DetalleOrden> detalles = new ArrayList<>();

        for (Pelicula p : peliculasEnCarrito) {
            totalPagado = totalPagado.add(p.getPrecioComprar());

            DetalleOrden detalle = new DetalleOrden();
            detalle.setPelicula(p);
            detalle.setPrecio(p.getPrecioComprar()); // Guarda el precio al momento de la compra
            // 'orden' se asignará en el paso 8
            detalles.add(detalle);
        }

        // 8. Crear la Orden de Compra
        OrdenCompra orden = new OrdenCompra();
        orden.setUsuario(usuario);
        orden.setFechaCompra(LocalDateTime.now());
        orden.setTotalPagado(totalPagado);
        orden.setEstado(EstadoOrden.PENDIENTE); // Empieza como PENDIENTE

        // Asigna la orden a cada detalle (relación bidireccional)
        for (DetalleOrden d : detalles) {
            d.setOrden(orden);
        }
        orden.setDetalles(detalles); // Asigna la lista de detalles a la orden

        // 9. Guardar la orden (esto guarda los detalles por Cascade)
        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);

        // --- 10. SIMULACIÓN DEL TRIGGER (Lógica de Negocio) ---
        // Como el pago fue exitoso, completamos la orden y añadimos a la biblioteca.
        simularTriggerCompletarOrden(ordenGuardada);

        // 11. Devolver el DTO de respuesta
        return mapOrdenToResponseDTO(ordenGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenResponseDTO> obtenerMisOrdenes(Pageable pageable) {
        Usuario usuario = obtenerUsuarioLogueado();
        Page<OrdenCompra> ordenes = ordenCompraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuario.getId(),
                pageable);
        return ordenes.map(this::mapOrdenToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponseDTO obtenerOrdenPorId(Long ordenId) {
        Usuario usuario = obtenerUsuarioLogueado();
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", ordenId));

        // ¡Validación de Seguridad!
        // Un usuario solo puede ver sus propias órdenes.
        if (!orden.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado. Esta orden no te pertenece.");
        }

        return mapOrdenToResponseDTO(orden);
    }

    // --- MÉTODO DE ADMIN ---

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenResponseDTO> obtenerTodasLasOrdenes(Pageable pageable) {
        Page<OrdenCompra> ordenes = ordenCompraRepository.findAll(pageable);
        return ordenes.map(this::mapOrdenToResponseDTO);
    }

    // --- MÉTODOS HELPER (Privados) ---

    /**
     * (HELPER) Simula el Trigger de la BD:
     * Cambia el estado a COMPLETADA y añade las películas a la biblioteca.
     */
    private void simularTriggerCompletarOrden(OrdenCompra orden) {
        orden.setEstado(EstadoOrden.COMPLETADA);

        List<UsuarioPelicula> nuevaBiblioteca = new ArrayList<>();

        for (DetalleOrden detalle : orden.getDetalles()) {
            UsuarioPelicula itemBiblioteca = new UsuarioPelicula();
            itemBiblioteca.setUsuario(orden.getUsuario());
            itemBiblioteca.setPelicula(detalle.getPelicula());
            itemBiblioteca.setFechaCompra(orden.getFechaCompra());
            itemBiblioteca.setPrecioCompra(detalle.getPrecio());

            nuevaBiblioteca.add(itemBiblioteca);
        }

        // Guarda todos los nuevos ítems en la biblioteca
        usuarioPeliculaRepository.saveAll(nuevaBiblioteca);
        // Actualiza el estado de la orden
        ordenCompraRepository.save(orden);
    }

    /**
     * (HELPER) Obtiene el usuario logueado desde el SecurityContext.
     */
    private Usuario obtenerUsuarioLogueado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = authentication.getName();
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el token: " + emailUsuario));
    }

    // --- Mapeadores de DTO ---

    private OrdenResponseDTO mapOrdenToResponseDTO(OrdenCompra orden) {
        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setId(orden.getId());
        dto.setEstado(orden.getEstado());
        dto.setFechaCompra(orden.getFechaCompra());
        dto.setTotalPagado(orden.getTotalPagado());

        // Mapea la lista de detalles
        dto.setDetalles(orden.getDetalles().stream()
                .map(this::mapDetalleToResponseDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private DetalleOrdenResponseDTO mapDetalleToResponseDTO(DetalleOrden detalle) {
        DetalleOrdenResponseDTO dto = new DetalleOrdenResponseDTO();
        dto.setId(detalle.getId());
        dto.setPrecio(detalle.getPrecio());
        // Mapea el resumen de la película
        dto.setPelicula(mapPeliculaToResumenDTO(detalle.getPelicula()));
        return dto;
    }

    private PeliculaResumenDTO mapPeliculaToResumenDTO(Pelicula pelicula) {
        // (Este es un helper simple, podríamos tener un Mapeador global)
        PeliculaResumenDTO dto = new PeliculaResumenDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setImgFrente(pelicula.getImgFrente());
        dto.setAnioLanzamiento(pelicula.getAnioLanzamiento());
        dto.setPrecioComprar(pelicula.getPrecioComprar());
        return dto;
    }
}
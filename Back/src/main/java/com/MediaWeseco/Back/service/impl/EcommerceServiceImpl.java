package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.CarritoResponseDto;
import com.MediaWeseco.Back.dtos.ItemCarritoDto;
import com.MediaWeseco.Back.dtos.SuscripcionStatusDto;
import com.MediaWeseco.Back.models.*;
import com.MediaWeseco.Back.repository.*;
import com.MediaWeseco.Back.service.EcommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EcommerceServiceImpl implements EcommerceService {

    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final CarritoRepository carritoRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final PedidoRepository pedidoRepository;
    private final PlanRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;

    // --- LÓGICA DE CARRITO ---

    @Override
    public void agregarAlCarrito(Long usuarioId, Long contenidoId) {
        // 1. Validaciones
        if (carritoRepository.existsByUsuarioIdAndContenidoId(usuarioId, contenidoId)) {
            throw new RuntimeException("Este contenido ya está en tu carrito.");
        }
        if (bibliotecaRepository.existsByUsuarioIdAndContenidoId(usuarioId, contenidoId)) {
            throw new RuntimeException("¡Ya compraste este contenido!");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Contenido contenido = contenidoRepository.findById(contenidoId).orElseThrow();

        // 2. Crear Item
        ItemCarrito item = new ItemCarrito();
        item.setUsuario(usuario);
        item.setContenido(contenido);
        item.setFechaAgregado(LocalDateTime.now());

        carritoRepository.save(item);
    }

    @Override
    public void eliminarDelCarrito(Long itemCarritoId, Long usuarioId) {
        ItemCarrito item = carritoRepository.findById(itemCarritoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No puedes borrar el carrito de otro.");
        }
        carritoRepository.delete(item);
    }

    @Override
    public CarritoResponseDto verCarrito(Long usuarioId) {
        List<ItemCarrito> items = carritoRepository.findByUsuarioId(usuarioId);

        BigDecimal total = BigDecimal.ZERO;

        // Convertir Entidades a DTOs y sumar precios
        List<ItemCarritoDto> itemsDto = items.stream().map(i -> {
            BigDecimal precio = i.getContenido().getPrecio();
            if (precio == null)
                precio = BigDecimal.ZERO;

            return ItemCarritoDto.builder()
                    .id(i.getId())
                    .contenidoId(i.getContenido().getId())
                    .titulo(i.getContenido().getTitulo())
                    .imgPortada(i.getContenido().getImgPoster())
                    .precio(precio)
                    .build();
        }).collect(Collectors.toList());

        // Calcular total
        for (ItemCarritoDto item : itemsDto) {
            total = total.add(item.getPrecio());
        }

        return CarritoResponseDto.builder()
                .items(itemsDto)
                .total(total)
                .build();
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {

        List<ItemCarrito> items = carritoRepository.findByUsuarioId(usuarioId);
        carritoRepository.deleteAll(items);
    }

    // --- LÓGICA DE COMPRAS (TRANSACTIONAL) ---

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void comprarCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        List<ItemCarrito> items = carritoRepository.findByUsuarioId(usuarioId);

        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        // 1. Calcular Total
        BigDecimal total = items.stream()
                .map(i -> i.getContenido().getPrecio() != null ? i.getContenido().getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Crear Registro de Pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        // 4. Mover items a Biblioteca (Propiedad)
        for (ItemCarrito item : items) {
            Biblioteca propiedad = new Biblioteca();
            propiedad.setUsuario(usuario);
            propiedad.setContenido(item.getContenido());
            propiedad.setFechaAdquisicion(LocalDateTime.now());
            bibliotecaRepository.save(propiedad);
        }

        // 5. Vaciar Carrito
        carritoRepository.deleteAll(items);
    }

    @Override
    @Transactional
    public void comprarSuscripcion(Long usuarioId, Integer planId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Suscripcion sus = new Suscripcion();
        sus.setUsuario(usuario);
        sus.setPlan(plan);
        sus.setFechaInicio(LocalDateTime.now());
        sus.setFechaFin(LocalDateTime.now().plusDays(plan.getDuracionDias()));
        sus.setEstaActiva(true);

        suscripcionRepository.save(sus);
    }

    @Override
    public SuscripcionStatusDto getEstadoSuscripcion(Long usuarioId) {
        Optional<Suscripcion> susOpt = suscripcionRepository.findActiveSubscription(usuarioId);

        if (susOpt.isPresent()) {
            Suscripcion s = susOpt.get();
            long dias = ChronoUnit.DAYS.between(LocalDateTime.now(), s.getFechaFin());

            return SuscripcionStatusDto.builder()
                    .activa(true)
                    .nombrePlan(s.getPlan().getNombre())
                    .fechaFin(s.getFechaFin())
                    .diasRestantes(dias)
                    .build();
        } else {
            return SuscripcionStatusDto.builder()
                    .activa(false)
                    .nombrePlan("Gratuito")
                    .diasRestantes(0)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCarritoDto> getBiblioteca(Long usuarioId) {
        List<Biblioteca> items = bibliotecaRepository.findByUsuarioId(usuarioId);

        return items.stream().map(i -> ItemCarritoDto.builder()
                .id(i.getContenido().getId())
                .contenidoId(i.getContenido().getId())
                .titulo(i.getContenido().getTitulo())
                .imgPortada(i.getContenido().getImgPoster())
                .precio(null)
                .build()).collect(Collectors.toList());
    }
}
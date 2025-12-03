package com.MediaWeseco.Back.service;

import java.util.List;

import com.MediaWeseco.Back.dtos.CarritoResponseDto;
import com.MediaWeseco.Back.dtos.ItemCarritoDto;
import com.MediaWeseco.Back.dtos.SuscripcionStatusDto;

public interface EcommerceService {
    // --- CARRITO ---
    void agregarAlCarrito(Long usuarioId, Long contenidoId);

    void eliminarDelCarrito(Long itemCarritoId, Long usuarioId);

    CarritoResponseDto verCarrito(Long usuarioId);

    void vaciarCarrito(Long usuarioId);

    // --- COMPRAS (Transaccional) ---
    void comprarCarrito(Long usuarioId); // Mueve items a Biblioteca

    void comprarSuscripcion(Long usuarioId, Integer planId);

    // --- ESTADO ---
    SuscripcionStatusDto getEstadoSuscripcion(Long usuarioId);

    List<ItemCarritoDto> getBiblioteca(Long usuarioId);
}
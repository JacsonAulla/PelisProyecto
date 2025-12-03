package com.MediaWeseco.Back.controllers;

import com.MediaWeseco.Back.config.security.UserDetailsImpl;
import com.MediaWeseco.Back.dtos.CarritoResponseDto;
import com.MediaWeseco.Back.dtos.ItemCarritoDto;
import com.MediaWeseco.Back.dtos.SuscripcionStatusDto;
import com.MediaWeseco.Back.service.AccessService;
import com.MediaWeseco.Back.service.EcommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ecommerce")
@RequiredArgsConstructor
public class EcommerceController {

    private final EcommerceService ecommerceService;
    private final AccessService accessService;

    @GetMapping("/cart")
    public ResponseEntity<CarritoResponseDto> viewCart(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(ecommerceService.verCarrito(user.getId()));
    }

    @PostMapping("/cart/add/{contenidoId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long contenidoId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            ecommerceService.agregarAlCarrito(user.getId(), contenidoId);
            return ResponseEntity.ok(Map.of("message", "Agregado al carrito"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            ecommerceService.eliminarDelCarrito(itemId, user.getId());
            return ResponseEntity.ok(Map.of("message", "Eliminado del carrito"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetailsImpl user) {
        ecommerceService.vaciarCarrito(user.getId());
        return ResponseEntity.ok(Map.of("message", "Carrito vaciado"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal UserDetailsImpl user) {
        try {
            ecommerceService.comprarCarrito(user.getId());
            return ResponseEntity.ok(Map.of("message", "¡Compra exitosa! Contenido agregado a tu biblioteca."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<?> subscribe(
            @PathVariable Integer planId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            ecommerceService.comprarSuscripcion(user.getId(), planId);
            return ResponseEntity.ok(Map.of("message", "¡Suscripción activa!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/subscription")
    public ResponseEntity<SuscripcionStatusDto> getSubscriptionStatus(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(ecommerceService.getEstadoSuscripcion(user.getId()));
    }

    @GetMapping("/check-access/{contenidoId}")
    public ResponseEntity<?> checkAccess(
            @PathVariable Long contenidoId,
            @AuthenticationPrincipal UserDetailsImpl user) {

        boolean access = accessService.tieneAcceso(user.getId(), contenidoId);
        return ResponseEntity.ok(Map.of("hasAccess", access));
    }

    @GetMapping("/library")
    public ResponseEntity<List<ItemCarritoDto>> getMyLibrary(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(ecommerceService.getBiblioteca(user.getId()));
    }
}
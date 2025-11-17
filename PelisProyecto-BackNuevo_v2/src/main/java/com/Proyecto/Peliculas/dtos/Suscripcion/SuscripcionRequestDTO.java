package com.Proyecto.Peliculas.dtos.Suscripcion;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuscripcionRequestDTO {
    
    // Un token/ID que simula venir de un procesador de pagos (ej. Stripe, MercadoPago)
    // para confirmar que el pago se realizó en el front-end.
    @NotBlank(message = "Se requiere un token de método de pago")
    private String paymentMethodToken;

    // Podríamos añadir un 'planId' si tuvieras varios planes
}


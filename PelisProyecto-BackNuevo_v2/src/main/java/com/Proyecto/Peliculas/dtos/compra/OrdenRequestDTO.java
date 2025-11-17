package com.Proyecto.Peliculas.dtos.compra;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrdenRequestDTO {

    // Una lista de los IDs de las películas en el carrito
    @NotEmpty(message = "El carrito no puede estar vacío")
    private List<Long> peliculasId;

    // Al igual que con la suscripción, aquí iría un
    // 'paymentMethodToken' para el pago real.
    @NotEmpty(message = "Se requiere un token de método de pago")
    private String paymentMethodToken;
}
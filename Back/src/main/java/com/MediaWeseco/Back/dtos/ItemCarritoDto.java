package com.MediaWeseco.Back.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ItemCarritoDto {
    private Long id; // ID del item en el carrito
    private Long contenidoId;
    private String titulo;
    private String imgPortada;
    private BigDecimal precio;
}
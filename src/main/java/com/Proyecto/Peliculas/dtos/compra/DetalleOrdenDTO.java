package com.Proyecto.Peliculas.dtos.compra;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenDTO {
    private Long id;
    private Long peliculaId;
    private String peliculaTitulo;
    private BigDecimal precio;
}
package com.Proyecto.Peliculas.dtos.Suscripcion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.Proyecto.Peliculas.enums.EstadoSuscripcion;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuscripcionResponseDTO {
    private Long id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoSuscripcion estado;
    private BigDecimal precioPagado;
    
    // Omitimos el objeto 'Usuario' para evitar bucles JSON
}
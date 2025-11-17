package com.Proyecto.Peliculas.dtos.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.Proyecto.Peliculas.enums.EstadoOrden;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrdenResponseDTO {
    private Long id;
    private EstadoOrden estado;
    private LocalDateTime fechaCompra;
    private BigDecimal totalPagado;
    
    // La lista de ítems que se compraron en esta orden
    private List<DetalleOrdenResponseDTO> detalles;

    // Omitimos el 'Usuario' para evitar bucles
}
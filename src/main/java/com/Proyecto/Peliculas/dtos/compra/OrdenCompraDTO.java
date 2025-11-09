package com.Proyecto.Peliculas.dtos.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.Proyecto.Peliculas.enums.EstadoOrden;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    private Long usuarioId;
    private EstadoOrden estado;
    private LocalDateTime fechaCompra;
    private BigDecimal totalPagado;
    private List<DetalleOrdenDTO> detalles;
}

package com.Proyecto.Peliculas.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.Proyecto.Peliculas.enums.EstadoOrden;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "orden_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"detalles"})
@ToString(exclude = {"detalles"})
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;
    
    @Column(name = "total_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagado;
    
    // Relación uno-a-muchos con DetalleOrden
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();

}
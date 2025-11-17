package com.Proyecto.Peliculas.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario_pelicula", uniqueConstraints = {
    // Esto mapea el "UNIQUE KEY unique_usuario_pelicula (usuario_id, pelicula_id)"
    // de tu base de datos. Es una buena práctica para que JPA lo sepa.
    @UniqueConstraint(columnNames = {"usuario_id", "pelicula_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relación con Usuario ---
    // Reemplaza 'private Long usuarioId'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore // <-- VITAL: para evitar bucle (Usuario -> Biblioteca -> Usuario)
    private Usuario usuario;

    // --- Relación con Pelicula ---
    // Reemplaza 'private Long peliculaId'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", nullable = false)
    // SIN @JsonIgnore: Queremos ver los datos de la película en la biblioteca
    private Pelicula pelicula;

    // --- Atributos Extra ---
    
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

}
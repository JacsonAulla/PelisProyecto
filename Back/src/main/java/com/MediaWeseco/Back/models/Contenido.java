package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contenido")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "anio_lanzamiento")
    private Integer anioLanzamiento;

    @Column(name = "img_poster")
    private String imgPoster;

    @Column(name = "img_banner")
    private String imgBanner;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @ManyToOne
    @JoinColumn(name = "tipo_contenido_id", nullable = false)
    private TipoContenido tipoContenido;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "contenido_genero", joinColumns = @JoinColumn(name = "contenido_id"), inverseJoinColumns = @JoinColumn(name = "genero_id"))
    private Set<Genero> generos = new HashSet<>();
}
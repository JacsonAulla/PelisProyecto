package com.Proyecto.Peliculas.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "peliculas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "anio_lanzamiento")
    private Integer anioLanzamiento;
    
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;
    
    @Column(name = "disponible")
    private Boolean disponible;
    
    @Column(name = "precio_comprar", precision = 10, scale = 2)
    private BigDecimal precioComprar;

    @Column(name = "img_frente", length = 255)
    private String imgFrente;
    
    @Column(name = "url_stream", length = 1024)
    private String urlStream;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pelicula_genero", joinColumns = @JoinColumn(name = "pelicula_id"), inverseJoinColumns = @JoinColumn(name = "genero_id"))
    private List<Genero> generos = new ArrayList<>();
    
    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImagenPelicula> imagenes = new ArrayList<>();
}
package com.Proyecto.Peliculas.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagenes_pelicula")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenPelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "url_imagen", nullable = false, length = 255)
    private String urlImagen;
    
    // Relación muchos-a-uno con Pelicula
    @ManyToOne
    @JoinColumn(name = "pelicula_id", nullable = false)
    @JsonIgnore
    private Pelicula pelicula;

}
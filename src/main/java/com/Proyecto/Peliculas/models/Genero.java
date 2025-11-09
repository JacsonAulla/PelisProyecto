package com.Proyecto.Peliculas.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "generos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"peliculas"})
@ToString(exclude = {"peliculas"})
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
    
    // Relación inversa (opcional, solo si necesitas acceder desde Genero a Peliculas)
    @ManyToMany(mappedBy = "generos")
    @JsonIgnore
    private List<Pelicula> peliculas = new ArrayList<>();

}
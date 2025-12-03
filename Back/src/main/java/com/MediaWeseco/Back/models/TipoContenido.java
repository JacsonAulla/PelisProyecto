package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_contenido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoContenido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
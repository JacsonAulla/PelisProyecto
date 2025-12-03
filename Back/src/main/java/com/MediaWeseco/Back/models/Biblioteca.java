package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biblioteca")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Biblioteca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "contenido_id")
    private Contenido contenido;

    @Column(name = "fecha_adquisicion")
    private LocalDateTime fechaAdquisicion;
}
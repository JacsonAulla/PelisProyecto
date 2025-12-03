package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peliculas")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula extends Contenido {

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "url_stream")
    private String urlStream;

    @Column(name = "formato_stream")
    private String formatoStream;
}
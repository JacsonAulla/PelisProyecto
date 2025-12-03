package com.MediaWeseco.Back.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "episodios", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "temporada_id", "numero_episodio" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_episodio", nullable = false)
    private Integer numeroEpisodio;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "url_stream", nullable = false)
    private String urlStream;

    @Column(name = "img_miniatura")
    private String imgMiniatura;

    @ManyToOne
    @JoinColumn(name = "temporada_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Temporada temporada;
}
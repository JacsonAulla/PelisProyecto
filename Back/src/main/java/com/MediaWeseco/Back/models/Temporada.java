package com.MediaWeseco.Back.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "temporadas", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "serie_id", "numero_temporada" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_temporada", nullable = false)
    private Integer numeroTemporada;

    private String titulo; // Opcional

    @Column(name = "fecha_estreno")
    private LocalDate fechaEstreno;

    // Relación hacia arriba (Serie)
    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    @JsonIgnore // Evita bucles infinitos al convertir a JSON (Serie -> Temporada -> Serie...)
    @ToString.Exclude // Evita bucles en logs
    private Serie serie;

    // Relación hacia abajo (Episodios)
    @OneToMany(mappedBy = "temporada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episodio> episodios = new ArrayList<>();
}
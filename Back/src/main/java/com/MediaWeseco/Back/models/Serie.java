package com.MediaWeseco.Back.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Serie extends Contenido {

    @Column(name = "estado_serie")
    private String estadoSerie; // Ej: 'FINALIZADA'

    @Column(name = "total_temporadas")
    private Integer totalTemporadas;

    // Relación: Una Serie tiene muchas Temporadas
    // mappedBy = "serie" se refiere al nombre de la variable en la clase Temporada
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Temporada> temporadas = new ArrayList<>();
}
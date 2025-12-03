package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporadaDto {
    private Long id;

    @Min(1)
    private Integer numeroTemporada;

    private String titulo;
    private LocalDate fechaEstreno;

    // La temporada contiene una lista de episodios
    @Builder.Default
    private List<EpisodioDto> episodios = new ArrayList<>();
}
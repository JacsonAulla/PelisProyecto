package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieDto {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El slug es obligatorio")
    private String slug;

    private String descripcion;

    @Min(1900)
    private Integer anioLanzamiento;

    private String imgPortada;
    private String imgBanner;
    private BigDecimal precio;
    private Boolean estaActivo;

    private String estadoSerie;
    private Integer totalTemporadas;

    private List<Integer> generosIds;
    private List<GeneroDto> generos;

    @Builder.Default
    private List<TemporadaDto> temporadas = new ArrayList<>();
}
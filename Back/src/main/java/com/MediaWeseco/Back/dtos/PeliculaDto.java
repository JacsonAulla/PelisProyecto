package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaDto {

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

    @NotNull(message = "La duración es obligatoria")
    @Min(1)
    private Integer duracionMinutos;

    private String urlStream;

    private List<Integer> generosIds;
    private List<GeneroDto> generos;
}
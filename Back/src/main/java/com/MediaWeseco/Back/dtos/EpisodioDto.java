package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodioDto {
    private Long id;

    @Min(1)
    private Integer numeroEpisodio;

    @NotBlank
    private String titulo;

    private String descripcion;
    private Integer duracionMinutos;
    private String urlStream;
    private String imgMiniatura;
}
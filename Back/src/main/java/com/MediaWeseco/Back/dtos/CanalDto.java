package com.MediaWeseco.Back.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class CanalDto {
    private Long id;

    @NotBlank
    private String titulo;
    @NotBlank
    private String slug;
    private String descripcion;
    private String imgPortada;
    private String imgBanner;
    private BigDecimal precio;
    private Boolean estaActivo;
    @NotBlank
    private String urlStream;
    private String tipoTransmision;
    private String pais;
    private List<Integer> generosIds;
    private List<GeneroDto> generos;
}
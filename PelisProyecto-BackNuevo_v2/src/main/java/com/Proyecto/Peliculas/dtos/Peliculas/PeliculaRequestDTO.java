package com.Proyecto.Peliculas.dtos.Peliculas;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PeliculaRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 150)
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El año de lanzamiento es requerido")
    private Integer anioLanzamiento;

    @NotNull(message = "La duración es requerida")
    @PositiveOrZero(message = "La duración debe ser un número positivo")
    private Integer duracionMinutos;

    private Boolean disponible = true;

    @NotNull(message = "El precio es requerido")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private BigDecimal precioComprar;

    private String imgFrente;
    private String urlStream;

    // Para asignar géneros, el front-end no nos envía objetos, nos envía una lista de los IDs de los géneros que seleccionó.
    private List<Long> generosId;
}
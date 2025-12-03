package com.MediaWeseco.Back.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SuscripcionStatusDto {
    private boolean activa;
    private String nombrePlan;
    private LocalDateTime fechaFin;
    private long diasRestantes;
}
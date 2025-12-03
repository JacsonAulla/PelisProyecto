package com.MediaWeseco.Back.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CarritoResponseDto {
    private List<ItemCarritoDto> items;
    private BigDecimal total;
}
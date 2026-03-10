package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CajaResumenDTO {

    private LocalDate fecha;
    private BigDecimal totalVentas;
    private BigDecimal utilidadDia;

}

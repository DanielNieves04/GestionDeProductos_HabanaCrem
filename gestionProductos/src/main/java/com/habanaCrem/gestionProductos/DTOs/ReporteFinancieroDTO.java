package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ReporteFinancieroDTO {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private BigDecimal totalVentas;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTransferencias;
    private BigDecimal totalGastos;
    private BigDecimal utilidadTotal;

    private List<CajaResumenDTO> cajas;
}

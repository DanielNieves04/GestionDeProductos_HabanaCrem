package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class VentasDiariasDTO {

    private LocalDate fecha;
    private BigDecimal totalLocal;
    private BigDecimal totalMayorista;
    private BigDecimal totalVentas;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTransferencias;
    private BigDecimal totalGastos;
    private BigDecimal utilidadDia;

}

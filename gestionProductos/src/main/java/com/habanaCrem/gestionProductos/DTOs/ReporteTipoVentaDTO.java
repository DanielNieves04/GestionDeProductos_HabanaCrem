package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ReporteTipoVentaDTO {

    private BigDecimal totalLocal;
    private BigDecimal totalMayorista;

    private BigDecimal efectivoLocal;
    private BigDecimal efectivoMayorista;

    private BigDecimal transferenciasLocal;
    private BigDecimal transferenciasMayorista;
}

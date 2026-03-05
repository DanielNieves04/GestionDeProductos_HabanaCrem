package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DetalleVentaResponseDTO {
    private Integer id;
    private Integer productoId;
    private String nombreProducto;
    private Integer cantidadEntregada;
    private Integer cantidadDefectuosos;
    private Integer cantidadDevuelta;
    private BigDecimal subtotal;
    private BigDecimal gananciaMayorista;
}

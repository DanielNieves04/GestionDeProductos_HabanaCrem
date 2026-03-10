package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductoMasVendidoDTO {

    private String productoNombre;
    private Integer unidadesVendidas;
    private BigDecimal totalVentas;
}

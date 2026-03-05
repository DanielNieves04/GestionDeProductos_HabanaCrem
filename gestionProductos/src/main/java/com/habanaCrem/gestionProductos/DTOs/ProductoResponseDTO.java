package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductoResponseDTO {

    private String nombre;
    private BigDecimal precioLocal;
    private BigDecimal precioMayorista;
    private BigDecimal precioDefectuoso;
    private BigDecimal gananciaMayorista;
    private List<InventarioResponseDTO> inventarios;

}

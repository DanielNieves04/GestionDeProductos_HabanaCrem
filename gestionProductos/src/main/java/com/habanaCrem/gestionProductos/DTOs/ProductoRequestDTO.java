package com.habanaCrem.gestionProductos.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductoRequestDTO {

    @NotNull
    private String nombre;

    @PositiveOrZero
    private BigDecimal precioLocal;

    @PositiveOrZero
    private BigDecimal precioMayorista;

    @PositiveOrZero
    private BigDecimal precioDefectuoso;

    @PositiveOrZero
    private BigDecimal gananciaMayorista;

    @NotNull
    private List<InventarioRequestDTO> inventarios;

}

package com.habanaCrem.gestionProductos.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaRequestDTO {

    @NotNull
    private Integer productoId;

    @PositiveOrZero
    private Integer cantidadEntregada;

    @PositiveOrZero
    private Integer defectuosos;

    @PositiveOrZero
    private Integer devoluciones;
}

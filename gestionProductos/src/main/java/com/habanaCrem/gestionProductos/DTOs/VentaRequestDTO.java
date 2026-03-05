package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.TipoVenta;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class VentaRequestDTO {

    @NotNull
    private TipoVenta tipoVenta;

    private Integer mayoristaId;

    private BigDecimal transferenciaTotal;

    @NotEmpty
    private List<DetalleVentaRequestDTO> detalles;

}

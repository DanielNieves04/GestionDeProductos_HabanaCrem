package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.TipoGasto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GastoRequestDTO {

    @PositiveOrZero
    @NotNull
    private BigDecimal monto;

    @NotNull
    private String descripcion;

    @NotNull
    private TipoGasto tipoGasto;

}

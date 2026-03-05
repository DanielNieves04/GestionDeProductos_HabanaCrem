package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.Rol;
import com.habanaCrem.gestionProductos.Entity.TipoGasto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GastoResponseDTO {

    private Integer id;
    private LocalDate fecha;
    private BigDecimal monto;
    private String descripcion;
    private TipoGasto tipoGasto;
    private Rol rolUsuario;

}

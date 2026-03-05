package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.EstadoCaja;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CajaResponseDTO {
    private Integer idCaja;
    private LocalDate fechaApertura;
    private EstadoCaja estado;
}

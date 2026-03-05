package com.habanaCrem.gestionProductos.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioRequestDTO {

    @NotNull
    private Integer stockActual;

    @NotNull
    private Integer stockMedio;

    @NotNull
    private Integer stockBajo;

    @NotNull
    private Integer bodegaId;


}

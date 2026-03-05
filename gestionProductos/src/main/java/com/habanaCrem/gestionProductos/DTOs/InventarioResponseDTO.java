package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InventarioResponseDTO {

    private Integer stockActual;
    private Integer stockMedio;
    private Integer stockBajo;
    private String nombreBodega;
    private Integer bodegaId;

}

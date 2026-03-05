package com.habanaCrem.gestionProductos.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MayoristaResponseDTO {

    private String nombre;
    private String apellido;
    private String direccion;
    private Long telefono;
    private LocalDate fechaCumpleanos;

}

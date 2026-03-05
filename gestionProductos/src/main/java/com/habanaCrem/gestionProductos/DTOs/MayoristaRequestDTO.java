package com.habanaCrem.gestionProductos.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MayoristaRequestDTO {

    @NotNull
    private String nombre;

    @NotNull
    private String apellido;

    @NotNull
    private String direccion;

    @NotNull
    private Long telefono;

    @NotNull
    private LocalDate fechaCumpleanos;
}

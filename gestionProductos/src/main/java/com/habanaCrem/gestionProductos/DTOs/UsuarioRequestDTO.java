package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.Rol;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDTO {

    @NotNull
    private String email;

    @NotNull
    private String contrasena;

    @NotNull
    private Rol rol;

}

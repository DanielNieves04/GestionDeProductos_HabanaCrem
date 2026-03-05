package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.Rol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioResponseDTO {

    private Integer id;
    private String email;
    private Rol rol;

}

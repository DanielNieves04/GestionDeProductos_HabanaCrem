package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.MayoristaRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.MayoristaResponseDTO;

import java.util.List;

public interface MayoristaService {
    List<MayoristaResponseDTO> obtenerMayoristas();
    MayoristaResponseDTO registrarMayorista(MayoristaRequestDTO dto);
    void eliminarMayorista(Integer mayoristaId);
    MayoristaResponseDTO modificarMayorista(Integer mayoristaId, MayoristaRequestDTO dto);
}

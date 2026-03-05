package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.GastoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.GastoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface GastoService {

    GastoResponseDTO registrarGasto(GastoRequestDTO dto, Integer usuarioId);
    List<GastoResponseDTO> obtenerGastosPorRango(LocalDate inicio, LocalDate fin);
    GastoResponseDTO modificarGasto(Integer gastoId, GastoRequestDTO dto, Integer usuarioId);
    void eliminarGasto(Integer gastoId);
}

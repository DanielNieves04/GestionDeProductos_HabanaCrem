package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.CajaCierreResponeDTO;
import com.habanaCrem.gestionProductos.DTOs.CajaResponseDTO;
import com.habanaCrem.gestionProductos.Entity.Caja;

public interface CajaService {


    CajaResponseDTO abrirCaja(Integer idUsuario);
    CajaCierreResponeDTO cerrarCaja(Integer idUsuario);
    Caja validarCajaAbiertaHoy();
}

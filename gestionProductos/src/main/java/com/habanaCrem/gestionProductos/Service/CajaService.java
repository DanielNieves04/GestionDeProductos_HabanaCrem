package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.*;
import com.habanaCrem.gestionProductos.Entity.Caja;

import java.time.LocalDate;
import java.util.List;

public interface CajaService {

    List<VentasDiariasDTO> obtenerVentasDiariasPorRango(LocalDate fechaInicio, LocalDate fechaFin);
    CajaResponseDTO abrirCaja(Integer idUsuario);
    CajaCierreResponeDTO cerrarCaja(Integer idUsuario);
    ReporteFinancieroDTO generarReporte(LocalDate inicio, LocalDate fin);
    ReporteTipoVentaDTO reporteTipoDeVenta(LocalDate inicio, LocalDate fin);
    Caja validarCajaAbiertaHoy();
}

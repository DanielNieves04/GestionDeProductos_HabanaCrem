package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.*;
import com.habanaCrem.gestionProductos.Service.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    //  GET /caja/ventas-diarias?fechaInicio=2026-02-01&fechaFin=2026-02-01
    @GetMapping("/ventas-diarias")
    public ResponseEntity<List<VentasDiariasDTO>> obtenerVentasDiariasPorRango(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin
    ) {
        return ResponseEntity.ok(
                cajaService.obtenerVentasDiariasPorRango(fechaInicio, fechaFin)
        );
    }

    //   GET /caja/abrir/1
    @PostMapping("/abrir/{idUsuario}")
    public ResponseEntity<CajaResponseDTO> abrirCaja(@PathVariable Integer idUsuario){
        return ResponseEntity.ok(
                cajaService.abrirCaja(idUsuario)
        );
    }

    //   GET /caja/cerrar/1
    @PutMapping("/cerrar/{idUsuario}")
    public ResponseEntity<CajaCierreResponeDTO> cerrarCaja(@PathVariable Integer idUsuario){
        return ResponseEntity.ok(
                cajaService.cerrarCaja(idUsuario)
        );
    }

    //   GET /caja/reporte?inicio=2026-02-01&fin=2026-02-01
    @GetMapping("reporte")
    public ResponseEntity<ReporteFinancieroDTO> generarReporte(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ) {
        return ResponseEntity.ok(
                cajaService.generarReporte(inicio, fin)
        );
    }

    //   GET /caja/reporte-tipo-venta?inicio=2026-02-01&fin=2026-02-01
    @GetMapping("reporte-tipo-venta")
    public ResponseEntity<ReporteTipoVentaDTO> reporteTipoDeVenta(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ) {
        return ResponseEntity.ok(
                cajaService.reporteTipoDeVenta(inicio, fin)
        );
    }
}

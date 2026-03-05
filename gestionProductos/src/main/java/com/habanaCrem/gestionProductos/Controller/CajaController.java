package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.CajaCierreResponeDTO;
import com.habanaCrem.gestionProductos.DTOs.CajaResponseDTO;
import com.habanaCrem.gestionProductos.Service.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @PostMapping("/abrir/{idUsuario}")
    public ResponseEntity<CajaResponseDTO> abrirCaja(@PathVariable Integer idUsuario){
        return ResponseEntity.ok(
                cajaService.abrirCaja(idUsuario)
        );
    }

    @PutMapping("/cerrar/{idUsuario}")
    public ResponseEntity<CajaCierreResponeDTO> cerrarCaja(@PathVariable Integer idUsuario){
        return ResponseEntity.ok(
                cajaService.cerrarCaja(idUsuario)
        );
    }

}

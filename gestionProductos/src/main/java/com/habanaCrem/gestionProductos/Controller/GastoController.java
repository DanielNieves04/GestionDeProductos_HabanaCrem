package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.GastoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.GastoResponseDTO;
import com.habanaCrem.gestionProductos.Service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

    public final GastoService gastoService;

    //   GET /gastos/rango?inicio=2026-02-01&fin=2026-02-01
    @GetMapping("/rango")
    public ResponseEntity<List<GastoResponseDTO>> obtenerGastosPorRango(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
            ){
        return ResponseEntity.ok(
                gastoService.obtenerGastosPorRango(inicio, fin)
        );
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<GastoResponseDTO> registrarGasto(
        @PathVariable Integer usuarioId,
        @Valid @RequestBody GastoRequestDTO dto
    ){
        return ResponseEntity.ok(
                gastoService.registrarGasto(dto, usuarioId)
        );
    }

    @PutMapping("/{usuarioId}/{gastoId}")
    public ResponseEntity<GastoResponseDTO> modificarGasto(
            @PathVariable Integer gastoId,
            @PathVariable Integer usuarioId,
            @Valid @RequestBody GastoRequestDTO dto
    ){
        return ResponseEntity.ok(
                gastoService.modificarGasto(gastoId, dto, usuarioId)
        );
    }

    @DeleteMapping("/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(
            @PathVariable Integer gastoId
    ){
         gastoService.eliminarGasto(gastoId);
         return ResponseEntity.noContent().build();
    }

}

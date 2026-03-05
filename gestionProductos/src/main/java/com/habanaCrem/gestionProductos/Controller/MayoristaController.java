package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.MayoristaRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.MayoristaResponseDTO;
import com.habanaCrem.gestionProductos.Service.MayoristaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/mayorista")
@RequiredArgsConstructor
public class MayoristaController {

    private final MayoristaService mayoristaService;

    @GetMapping
    public ResponseEntity<List<MayoristaResponseDTO>> obtenerMayoristas(){
        return ResponseEntity.ok(
                mayoristaService.obtenerMayoristas()
        );
    }

    @PostMapping
    public ResponseEntity<MayoristaResponseDTO> registrarMayorista(
            @Valid @RequestBody MayoristaRequestDTO dto
    ){
        return ResponseEntity.ok(
                mayoristaService.registrarMayorista(dto)
        );
    }

    @PutMapping("/{mayoristaId}")
    public ResponseEntity<MayoristaResponseDTO> modificarMayorista(
            @PathVariable Integer mayoristaid,
            @Valid @RequestBody MayoristaRequestDTO dto
    ){
        return ResponseEntity.ok(
                mayoristaService.modificarMayorista(mayoristaid,dto)
        );
    }

    @DeleteMapping("/{mayoristaId}")
    public ResponseEntity<Void> eliminarMayorista(
            @PathVariable Integer mayoristaId
    ){
        mayoristaService.eliminarMayorista(mayoristaId);
        return ResponseEntity.noContent().build();
    }

}

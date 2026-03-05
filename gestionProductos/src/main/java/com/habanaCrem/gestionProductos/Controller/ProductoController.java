package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.ProductoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.ProductoResponseDTO;
import com.habanaCrem.gestionProductos.Service.ProductoService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    public final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos(){
        return ResponseEntity.ok(
                productoService.listarProductos()
        );
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorNombre(
            @PathVariable String nombre){
        return ResponseEntity.ok(
                productoService.obtenerProductoPorNombre(nombre)
        );
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<ProductoResponseDTO> registrarProducto(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody ProductoRequestDTO dto){
        return ResponseEntity.ok(
                productoService.registrarProducto(dto, usuarioId)
        );
    }

    @PutMapping("/{usuarioId}/{productoId}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Integer usuarioId,
            @PathVariable Integer productoId,
            @Valid @RequestBody ProductoRequestDTO dto){
        return ResponseEntity.ok(
                productoService.actualizarProducto(productoId, dto, usuarioId)
        );
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Integer productoId
    ){
        productoService.eliminarProducto(productoId);
        return ResponseEntity.noContent().build();
    }
}

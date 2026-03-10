package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.ProductoMasVendidoDTO;
import com.habanaCrem.gestionProductos.DTOs.VentaRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.VentaResponseDTO;
import com.habanaCrem.gestionProductos.Entity.TipoVenta;
import com.habanaCrem.gestionProductos.Exception.NegocioException;
import com.habanaCrem.gestionProductos.Service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<VentaResponseDTO> registrarVenta(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody VentaRequestDTO dto
    ){
        return ResponseEntity.ok(
                ventaService.registrarVenta(dto, usuarioId)
        );
    }

//    GET /ventas?inicio=2026-02-01&fin=2026-02-01
    @GetMapping()
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorFechas(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ){
        return ResponseEntity.ok(
                ventaService.obtenerVentasPorRango(inicio,fin)
        );
    }

//    GET /ventas/mayoristas/1/venta?fecha=2026-02-12
    @GetMapping("/mayoristas/{mayoristaId}/venta")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorFechaYmayorista(
            @PathVariable Integer mayoristaId,
            @RequestParam LocalDate fecha
    ){
        return ResponseEntity.ok(
                ventaService.obtenerVentaPorFechaYMayorista(fecha,mayoristaId)
        );
    }

    //    GET /ventas/tipoVenta/{tipoVenta}/venta?inicio=2026-02-01&fin=2026-02-01
    @GetMapping("/tipoVenta/{tipoVenta}/venta")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorFechas(
            @PathVariable TipoVenta tipoVenta,
            @RequestParam LocalDate fecha
    ){
        return ResponseEntity.ok(
                ventaService.obtenerVentaPorFechaYTipoventa(fecha,tipoVenta)
        );
    }

    // PUT /ventas/1
    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> modificarVenta(
        @PathVariable Integer id,
        @Valid @RequestBody VentaRequestDTO dto
    ){
        return ResponseEntity.ok(
                ventaService.modificarVenta(id,dto)
        );
    }

    // DELETE /ventas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer id){
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }

    // GET ventas/reporte/productos-mas-vendidos?inicio=2026-03-01&fin=2026-03-10
    @GetMapping("/reporte/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> obtenerTopProductos(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ){
        return ResponseEntity.ok(
                ventaService.obtenerTopProductos(inicio, fin)
        );
    }
}

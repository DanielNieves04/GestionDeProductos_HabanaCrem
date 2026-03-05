package com.habanaCrem.gestionProductos.DTOs;

import com.habanaCrem.gestionProductos.Entity.DetallesVenta;
import com.habanaCrem.gestionProductos.Entity.TipoVenta;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class VentaResponseDTO {

    private Integer id;
    private LocalDate fecha;
    private BigDecimal total;
    private TipoVenta tipoVenta;
    private String nombreMayorista;
    private List<DetalleVentaResponseDTO> detallesVenta;
}

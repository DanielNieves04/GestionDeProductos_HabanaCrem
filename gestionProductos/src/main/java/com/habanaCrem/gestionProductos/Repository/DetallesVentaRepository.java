package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.DTOs.ProductoMasVendidoDTO;
import com.habanaCrem.gestionProductos.Entity.DetallesVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DetallesVentaRepository extends JpaRepository<DetallesVenta, Integer> {
    @Query("""
            SELECT new com.habanaCrem.gestionProductos.DTOs.ProductoMasVendidoDTO(
                dv.producto.nombre,
                SUM(dv.cantidadEntregada),
                SUM(dv.total)
            )
            FROM DetalleVenta dv
            JOIN dv.venta v
            WHERE v.fecha BETWEEN :inicio AND :fin
            GROUP BY dv.producto.nombre
            ORDER BY SUM(dv.cantidadEntregada) DESC
    """)
    List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(
            LocalDate inicio,
            LocalDate fin
    );
}

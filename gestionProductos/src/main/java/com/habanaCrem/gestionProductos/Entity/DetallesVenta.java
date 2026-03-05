package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa el detalle de una venta.
 * Contiene la información del producto vendido, cantidades
 * y valores calculados al momento de la transacción.
 *
 * Nota: los totales se calculan en el backend para evitar
 * inconsistencias por datos manipulados desde el frontend.
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DetallesVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer cantidadEntregada;

    private Integer cantidadDefectuosos;

    private Integer cantidadDevuelta;

    @Column(nullable = false)
    private BigDecimal total;

    private BigDecimal gananciaMayorista;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}

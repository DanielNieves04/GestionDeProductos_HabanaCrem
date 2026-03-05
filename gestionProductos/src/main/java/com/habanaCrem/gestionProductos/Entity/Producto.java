package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precioLocal;

    @Column(nullable = false)
    private BigDecimal precioMayorista;

    @Column(nullable = false)
    private BigDecimal PrecioDefectuoso;

    @Column(nullable = false)
    private BigDecimal gananciaMayorista;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallesVenta> detallesVentas = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventario> inventarios = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProduccionDiaria> produccionesDiarias = new ArrayList<>();
}

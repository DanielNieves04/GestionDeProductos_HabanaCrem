package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String unidadMedida;

    @Column(nullable = false)
    private Integer stockMinimo;

    @Column(nullable = false)
    private Integer stockMaximo;

    private Integer stockActual;

    @Column(nullable = false)
    private Integer inventarioInicial;

    private String marca;

    private String descripcion;

    private String categoria;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaDetalle> formulaDetalles = new ArrayList<>();

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumoInsumo> consumoInsumos = new ArrayList<>();

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoInsumo> movimientoInsumos = new ArrayList<>();

}

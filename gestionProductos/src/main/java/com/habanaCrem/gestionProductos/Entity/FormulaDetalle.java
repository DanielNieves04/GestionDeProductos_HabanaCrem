package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "formula_detalle")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormulaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "formula_batido_id", nullable = false)
    private FormulaBatido formulaBatido;

    @ManyToOne
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;
}

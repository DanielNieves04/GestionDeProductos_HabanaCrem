package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formula_batido")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormulaBatido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String descripcion;

    @OneToMany(mappedBy = "formulaBatido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Batido> batidos = new ArrayList<>();

    @OneToMany(mappedBy = "formulaBatido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaDetalle> formulaDetalles = new ArrayList<>();
}

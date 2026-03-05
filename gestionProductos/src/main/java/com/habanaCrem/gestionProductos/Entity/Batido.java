package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
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
public class Batido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal costoProduccion;

    @OneToMany(mappedBy = "batido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProduccionDiaria> produccionesDiarias = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fomulaBatido_id", nullable = false)
    private FormulaBatido formulaBatido;
}

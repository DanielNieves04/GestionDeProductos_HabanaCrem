package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProduccionDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer unidadesProducidas;

    @Column(nullable = false)
    private Integer kilosBatidos;

    @Column(nullable = false)
    private BigDecimal costoUnidad;

    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "batido_id", nullable = false)
    private Batido batido;

    @OneToMany(mappedBy = "produccionDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumoInsumo> consumosInsumos = new ArrayList<>();
}

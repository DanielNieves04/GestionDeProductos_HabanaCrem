package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConsumoInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer cantidadUsada;

    @ManyToOne
    @JoinColumn(name = "produccionDiaria_id")
    private ProduccionDiaria produccionDiaria;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;
}

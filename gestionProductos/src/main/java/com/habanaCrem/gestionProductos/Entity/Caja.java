package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cajas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "fecha")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCaja estado;

    @Column(nullable = false)
    private LocalDateTime fechaHoraApertura;

    private LocalDateTime fechaHoraCierre;

    @ManyToOne
    @JoinColumn(name = "usuario_apertura_id", nullable = false)
    private Usuario usuarioApertura;

    @ManyToOne
    @JoinColumn(name = "usuario_cierre_id")
    private Usuario usuarioCierre;

    //Totales calculados al cierre de caja

    @Column(precision = 15, scale = 2)
    private BigDecimal totalVentas;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalTransferencias;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalEfectivo;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalGastos;

    @Column(precision = 15, scale = 2)
    private BigDecimal utilidadDia;
}

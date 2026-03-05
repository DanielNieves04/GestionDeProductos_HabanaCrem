package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVenta tipoVenta;

    private BigDecimal transferenciaTotal;

    @ManyToOne
    @JoinColumn(name ="usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany( mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallesVenta> detallesVentas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name ="mayorista_id", nullable = true)
    private Mayorista mayorista;
}

package com.habanaCrem.gestionProductos.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Mayorista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String direccion;

    @Column(nullable = false, unique = true)
    private Long telefono;

    private LocalDate fechaCumpleanos;

    @OneToMany(mappedBy = "mayorista", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Venta> ventas = new ArrayList<>();
}

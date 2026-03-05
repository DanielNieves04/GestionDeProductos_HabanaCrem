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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false, unique = true )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario")
    private List<Venta> ventas= new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Gasto> gastos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<ProduccionDiaria> produccionesDiarias = new ArrayList<>();

}




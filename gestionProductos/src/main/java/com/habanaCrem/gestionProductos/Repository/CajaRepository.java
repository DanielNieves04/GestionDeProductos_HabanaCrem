package com.habanaCrem.gestionProductos.Repository;


import com.habanaCrem.gestionProductos.Entity.Caja;
import com.habanaCrem.gestionProductos.Entity.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Integer> {

    boolean existsByFecha(
            LocalDate fecha
    );

    Optional<Caja> findByFecha(
            LocalDate fecha
    );

    List<Caja> findByFechaBetween(
            LocalDate inicio,
            LocalDate fin
    );

    List<Caja> findByFechaBetweenAndEstado(
            LocalDate inicio,
            LocalDate fin,
            EstadoCaja  estado
    );
}

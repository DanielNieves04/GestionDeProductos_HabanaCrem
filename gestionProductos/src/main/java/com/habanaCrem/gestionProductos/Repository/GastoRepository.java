package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto,Integer> {

    List<Gasto> findByFechaBetween(
            LocalDate inicio,
            LocalDate fin
    );

    List<Gasto> findByFecha(LocalDate fecha);

}

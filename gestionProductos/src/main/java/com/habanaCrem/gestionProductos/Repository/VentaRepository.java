package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.TipoVenta;
import com.habanaCrem.gestionProductos.Entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByFechaBetween(
            LocalDate inicio,
            LocalDate fin
    );

    Optional<Venta> findByFechaAndMayorista_Id(
            LocalDate fecha,
            Integer mayoristaId
    );

    List<Venta> findByFechaAndTipoVenta(
            LocalDate fecha,
            TipoVenta tipoVenta
    );

    List<Venta> findByFecha(LocalDate fecha);
}

package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario,Integer> {

    Optional<Inventario> findByProductoIdAndBodegaId(
            Integer productoId,
            Integer bodegaId
    );
}

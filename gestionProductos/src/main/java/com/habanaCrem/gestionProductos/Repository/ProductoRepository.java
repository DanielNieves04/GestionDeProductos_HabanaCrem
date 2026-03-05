package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto,Integer> {

    Optional<Producto> findByNombre(String nombre);
}

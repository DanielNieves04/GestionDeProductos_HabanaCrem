package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo,Integer> {
}

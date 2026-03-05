package com.habanaCrem.gestionProductos.Repository;

import com.habanaCrem.gestionProductos.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {

}

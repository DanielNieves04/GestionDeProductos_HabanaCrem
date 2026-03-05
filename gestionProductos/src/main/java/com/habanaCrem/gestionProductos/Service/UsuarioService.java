package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.UsuarioRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.UsuarioResponseDTO;
import com.habanaCrem.gestionProductos.Entity.Usuario;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la gestión de usuario.

 * Reglas de negocio principales:
 * - Toda venta es transaccional.
 * - El inventario se descuenta automáticamente al registrar una venta.
 * - Al eliminar una venta, el stock se revierte.
 * - No se permite stock negativo.

 * Este servicio centraliza la lógica crítica del módulo de ventas
 * para garantizar la integridad de inventario y datos financieros.
 */

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDTO> obtenerUsuarios(){

        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> UsuarioResponseDTO.builder()
                        .id(usuario.getId())
                        .email(usuario.getEmail())
                        .rol(usuario.getRol())
                        .build()
                ).toList();
    }

    public UsuarioResponseDTO obtenerUsuario(
            Integer id
    ){

        Usuario usuario = usuarioRepository.findById(id).
                orElseThrow(()->new RecursoNoEncontradoException("Usuario no encontrado"));

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    public UsuarioResponseDTO registrarUsuario(
            UsuarioRequestDTO dto
    ){
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasena(dto.getContrasena());
        usuario.setRol(dto.getRol());

        usuarioRepository.save(usuario);

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

}

package com.habanaCrem.gestionProductos.Controller;

import com.habanaCrem.gestionProductos.DTOs.UsuarioRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.UsuarioResponseDTO;
import com.habanaCrem.gestionProductos.Entity.Usuario;
import com.habanaCrem.gestionProductos.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/getAll")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuarios(){
        return ResponseEntity.ok(
                usuarioService.obtenerUsuarios()
        );
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable Integer id){
        return ResponseEntity.ok(
                usuarioService.obtenerUsuario(id)
        );
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @Valid @RequestBody UsuarioRequestDTO dto
    ){
        return ResponseEntity.ok(
                usuarioService.registrarUsuario(dto)
        );
    }

}

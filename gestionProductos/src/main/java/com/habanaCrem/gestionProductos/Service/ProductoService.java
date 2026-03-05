package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.ProductoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    List<ProductoResponseDTO> listarProductos();
    ProductoResponseDTO obtenerProductoPorNombre(String nombre);
    ProductoResponseDTO registrarProducto(ProductoRequestDTO dto, Integer idUsuario);
    ProductoResponseDTO actualizarProducto(Integer idProducto, ProductoRequestDTO dto, Integer idUsuario);
    void eliminarProducto(Integer idProducto);

}

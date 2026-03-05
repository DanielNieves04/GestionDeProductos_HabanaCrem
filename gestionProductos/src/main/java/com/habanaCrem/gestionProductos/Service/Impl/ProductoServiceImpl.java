package com.habanaCrem.gestionProductos.Service.Impl;

import com.habanaCrem.gestionProductos.DTOs.InventarioRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.InventarioResponseDTO;
import com.habanaCrem.gestionProductos.DTOs.ProductoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.ProductoResponseDTO;
import com.habanaCrem.gestionProductos.Entity.Bodega;
import com.habanaCrem.gestionProductos.Entity.Inventario;
import com.habanaCrem.gestionProductos.Entity.Producto;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.BodegaRepository;
import com.habanaCrem.gestionProductos.Repository.InventarioRepository;
import com.habanaCrem.gestionProductos.Repository.ProductoRepository;
import com.habanaCrem.gestionProductos.Repository.UsuarioRepository;
import com.habanaCrem.gestionProductos.Service.ProductoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de gestión de productos.
 *
 * Esta clase proporciona la lógica de negocio para las operaciones relacionadas con productos,
 * incluyendo: listado, búsqueda, registro, actualización y eliminación de productos.
 * También gestiona los inventarios asociados a cada producto en diferentes bodegas.
 *
 * @author Habana Crem
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    /** Repositorio para acceder y manipular datos de productos */
    public final ProductoRepository productoRepository;

    /** Repositorio para acceder y manipular datos de inventarios */
    public final InventarioRepository inventarioRepository;

    /** Repositorio para acceder y manipular datos de usuarios */
    public final UsuarioRepository usuarioRepository;

    /** Repositorio para acceder y manipular datos de bodegas */
    public final BodegaRepository bodegaRepository;

    /**
     * Obtiene el listado completo de todos los productos disponibles.
     *
     * Este método recupera todos los productos de la base de datos junto con sus inventarios
     * asociados en cada bodega, convirtiendo los datos en una respuesta DTO.
     *
     * @return Lista de {@link ProductoResponseDTO} con los detalles de cada producto y sus inventarios
     */
    @Override
    public List<ProductoResponseDTO> listarProductos() {

        List<Producto> productos = productoRepository.findAll();

        return productos.stream()
                .map(producto -> ProductoResponseDTO.builder()
                        .nombre(producto.getNombre())
                        .precioLocal(producto.getPrecioLocal())
                        .precioDefectuoso(producto.getPrecioDefectuoso())
                        .precioMayorista(producto.getPrecioMayorista())
                        .gananciaMayorista(producto.getGananciaMayorista())
                        .inventarios(producto.getInventarios().stream()
                                .map(inventario -> InventarioResponseDTO.builder()
                                        .stockActual(inventario.getStockActual())
                                        .stockMedio(inventario.getStockMedio())
                                        .stockBajo(inventario.getStockBajo())
                                        .nombreBodega(inventario.getBodega().getNombre())
                                        .bodegaId(inventario.getBodega().getId())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }
    /**
     * Obtiene un producto específico por su nombre.
     *
     * Este método busca un producto en la base de datos utilizando su nombre como identificador único.
     * Si el producto no existe, lanza una excepción de recurso no encontrado.
     *
     * @param nombre el nombre del producto a buscar (case-sensitive)
     * @return {@link ProductoResponseDTO} con los detalles del producto encontrado
     * @throws RecursoNoEncontradoException si el producto con el nombre especificado no existe
     */
    @Override
    public ProductoResponseDTO obtenerProductoPorNombre(String nombre) {

        Producto producto = productoRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        return ProductoResponseDTO.builder()
                .nombre(producto.getNombre())
                .precioLocal(producto.getPrecioLocal())
                .precioDefectuoso(producto.getPrecioDefectuoso())
                .precioMayorista(producto.getPrecioMayorista())
                .gananciaMayorista(producto.getGananciaMayorista())
                .inventarios(producto.getInventarios().stream()
                        .map(inventario -> InventarioResponseDTO.builder()
                                .stockActual(inventario.getStockActual())
                                .stockMedio(inventario.getStockMedio())
                                .stockBajo(inventario.getStockBajo())
                                .nombreBodega(inventario.getBodega().getNombre())
                                .bodegaId(inventario.getBodega().getId())
                                .build())
                        .toList())
                .build();
    }
    /**
     * Registra un nuevo producto en el sistema con sus inventarios asociados.
     *
     * Este método crea un nuevo producto con sus respectivos inventarios en diferentes bodegas.
     * Se valida que el usuario que realiza la operación exista en el sistema.
     * Después de la creación, se persisten automáticamente el producto y sus inventarios.
     *
     * Flujo del proceso:
     * 1. Valida que el usuario con idUsuario existe
     * 2. Crea una nueva entidad Producto con los datos del DTO
     * 3. Para cada inventario en el DTO:
     *    - Valida que la bodega existe
     *    - Crea y asocia un nuevo Inventario al producto
     * 4. Persiste el producto y sus inventarios en la base de datos
     *
     * @param dto {@link ProductoRequestDTO} con los datos del producto y sus inventarios
     * @param idUsuario identificador del usuario que realiza la operación
     * @return {@link ProductoResponseDTO} con los datos del producto creado
     * @throws RecursoNoEncontradoException si el usuario o alguna bodega no existen
     *
     * @see ProductoRequestDTO
     * @see ProductoResponseDTO
     */
    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(
            ProductoRequestDTO dto,
            Integer idUsuario
    ) {

        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecioLocal(dto.getPrecioLocal());
        producto.setPrecioMayorista(dto.getPrecioMayorista());
        producto.setPrecioDefectuoso(dto.getPrecioDefectuoso());
        producto.setGananciaMayorista(dto.getGananciaMayorista());

        for (InventarioRequestDTO inv : dto.getInventarios()) {

            Bodega bodega = bodegaRepository.findById(inv.getBodegaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Bodega no encontrada"));

            Inventario inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStockActual(inv.getStockActual());
            inventario.setStockMedio(inv.getStockMedio());
            inventario.setStockBajo(inv.getStockBajo());
            inventario.setBodega(bodega);

            producto.getInventarios().add(inventario);
        }

        productoRepository.save(producto);

        return ProductoResponseDTO.builder()
                .nombre(producto.getNombre())
                .precioLocal(producto.getPrecioLocal())
                .precioMayorista(producto.getPrecioMayorista())
                .precioDefectuoso(producto.getPrecioDefectuoso())
                .gananciaMayorista(producto.getGananciaMayorista())
                .build();
    }
    /**
     * Actualiza los datos de un producto existente y sus inventarios.
     *
     * Este método permite modificar todos los atributos de un producto (precios, nombre, ganancia),
     * así como los inventarios asociados en cada bodega. Los inventarios anteriores se eliminan
     * y se reemplazan con los nuevos datos proporcionados.
     *
     * Flujo del proceso:
     * 1. Valida que el usuario con idUsuario existe
     * 2. Busca el producto con idProducto
     * 3. Elimina todos los inventarios asociados al producto
     * 4. Actualiza los atributos del producto
     * 5. Crea nuevos inventarios con los datos del DTO para cada bodega
     * 6. Persiste los cambios en la base de datos
     *
     * @param idProducto identificador único del producto a actualizar
     * @param dto {@link ProductoRequestDTO} con los nuevos datos del producto e inventarios
     * @param idUsuario identificador del usuario que realiza la operación
     * @return {@link ProductoResponseDTO} con los datos actualizados del producto
     * @throws RecursoNoEncontradoException si el producto, usuario o alguna bodega no existen
     *
     * @see ProductoRequestDTO
     * @see ProductoResponseDTO
     */
    @Override
    public ProductoResponseDTO actualizarProducto(
            Integer idProducto,
            ProductoRequestDTO dto,
            Integer idUsuario)
    {

        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        producto.getInventarios().clear();

        producto.setNombre(dto.getNombre());
        producto.setPrecioMayorista(dto.getPrecioMayorista());
        producto.setPrecioLocal(dto.getPrecioLocal());
        producto.setPrecioDefectuoso(dto.getPrecioDefectuoso());
        producto.setGananciaMayorista(dto.getGananciaMayorista());

        for (InventarioRequestDTO inv : dto.getInventarios()) {

            Bodega bodega = bodegaRepository.findById(inv.getBodegaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Bodega no encontrada"));

            Inventario inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStockActual(inv.getStockActual());
            inventario.setStockMedio(inv.getStockMedio());
            inventario.setStockBajo(inv.getStockBajo());
            inventario.setBodega(bodega);

            producto.getInventarios().add(inventario);
        }

        productoRepository.save(producto);

        return ProductoResponseDTO.builder()
                .nombre(producto.getNombre())
                .precioLocal(producto.getPrecioLocal())
                .precioDefectuoso(producto.getPrecioDefectuoso())
                .precioMayorista(producto.getPrecioMayorista())
                .gananciaMayorista(producto.getGananciaMayorista())
                .build();
    }
    /**
     * Elimina un producto del sistema.
     *
     * Este método busca el producto por su identificador y lo elimina de la base de datos.
     * También elimina automáticamente todos los inventarios asociados a este producto
     * debido a la relación de cascade configurada en la entidad.
     *
     * @param idProducto identificador único del producto a eliminar
     * @throws RecursoNoEncontradoException si el producto con el identificador especificado no existe
     */
    @Override
    public void eliminarProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        productoRepository.delete(producto);
    }

}

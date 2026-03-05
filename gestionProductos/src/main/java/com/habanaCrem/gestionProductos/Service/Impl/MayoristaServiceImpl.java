package com.habanaCrem.gestionProductos.Service.Impl;

import com.habanaCrem.gestionProductos.DTOs.MayoristaRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.MayoristaResponseDTO;
import com.habanaCrem.gestionProductos.DTOs.VentaRequestDTO;
import com.habanaCrem.gestionProductos.Entity.Mayorista;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.MayoristaRepository;
import com.habanaCrem.gestionProductos.Service.MayoristaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de mayoristas.
 * <p>
 * Este servicio proporciona operaciones CRUD para los mayoristas del sistema,
 * incluyendo crear, leer, actualizar y eliminar mayoristas. Todas las operaciones
 * están envueltas en transacciones para garantizar la consistencia de los datos.
 * </p>
 * <p>
 * Utiliza el patrón DTO (Data Transfer Object) para la transferencia de datos
 * entre capas, separando la entidad persistente de los datos transmitidos.
 * </p>
 *
 * @author Sistema Habana Crem
 * @version 1.0
 * @since 2026
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MayoristaServiceImpl implements MayoristaService {

    /** Repositorio para acceder a los datos de mayoristas en la base de datos */
    private final MayoristaRepository mayoristaRepository;

    /**
     * Obtiene la lista de todos los mayoristas registrados en el sistema.
     * <p>
     * Este metodo recupera todos los mayoristas de la base de datos y los
     * transforma a DTOs de respuesta para su envío al cliente.
     * </p>
     *
     * @return Una lista de {@link MayoristaResponseDTO} con los datos de todos
     *         los mayoristas. Si no hay mayoristas, retorna una lista vacía.
     *
     * @see MayoristaResponseDTO
     */
    @Override
    public List<MayoristaResponseDTO> obtenerMayoristas() {

        List<Mayorista> mayoristas = mayoristaRepository.findAll();
        return mayoristas.stream()
                .map(mayorista -> MayoristaResponseDTO.builder()
                        .nombre(mayorista.getNombre())
                        .apellido(mayorista.getApellido())
                        .direccion(mayorista.getDireccion())
                        .telefono(mayorista.getTelefono())
                        .fechaCumpleanos(mayorista.getFechaCumpleanos())
                        .build()
                ).toList();
    }

    /**
     * Registra un nuevo mayorista en el sistema.
     * <p>
     * Este metodo crea un nuevo mayorista a partir de los datos proporcionados en el DTO,
     * los persiste en la base de datos y retorna un DTO de respuesta con los datos guardados.
     * La operación es transaccional, lo que garantiza que todos los cambios se apliquen
     * de manera atomística.
     * </p>
     *
     * @param dto {@link MayoristaRequestDTO} que contiene los datos del nuevo mayorista
     *            (nombre, apellido, dirección, teléfono, fecha de cumpleaños)
     *
     * @return {@link MayoristaResponseDTO} con los datos del mayorista recién registrado
     *
     * @throws IllegalArgumentException Si el DTO contiene datos inválidos
     *
     * @see MayoristaRequestDTO
     * @see MayoristaResponseDTO
     */
    @Override
    @Transactional
    public MayoristaResponseDTO registrarMayorista(MayoristaRequestDTO dto) {

        Mayorista mayorista = new Mayorista();
        mayorista.setNombre(dto.getNombre());
        mayorista.setApellido(dto.getApellido());
        mayorista.setDireccion(dto.getDireccion());
        mayorista.setTelefono(dto.getTelefono());
        mayorista.setFechaCumpleanos(dto.getFechaCumpleanos());

        mayoristaRepository.save(mayorista);

        return MayoristaResponseDTO.builder()
                .nombre(mayorista.getNombre())
                .apellido(mayorista.getApellido())
                .direccion(mayorista.getDireccion())
                .telefono(mayorista.getTelefono())
                .fechaCumpleanos(mayorista.getFechaCumpleanos())
                .build();
    }

    /**
     * Elimina un mayorista del sistema por su identificador.
     * <p>
     * Este metodo busca un mayorista en la base de datos por su ID y lo elimina.
     * La operación es transaccional para garantizar la consistencia de los datos.
     * </p>
     *
     * @param mayoristaId Identificador único del mayorista a eliminar
     *
     * @throws RecursoNoEncontradoException Si no existe un mayorista con el ID proporcionado
     *
     * @see RecursoNoEncontradoException
     */
    @Override
    @Transactional
    public void eliminarMayorista(Integer mayoristaId) {
        Mayorista mayorista = mayoristaRepository.findById(mayoristaId)
                .orElseThrow(()-> new RecursoNoEncontradoException("Mayorista no encontrado"));

        mayoristaRepository.delete(mayorista);
    }

    /**
     * Modifica los datos de un mayorista existente.
     * <p>
     * Este metodo busca un mayorista en la base de datos por su ID y actualiza todos sus
     * datos (nombre, apellido, dirección, teléfono y fecha de cumpleaños) con los valores
     * proporcionados en el DTO de solicitud. Luego persiste los cambios y retorna un DTO
     * de respuesta con los datos actualizados.
     * </p>
     *
     * @param mayoristaId Identificador único del mayorista a modificar
     * @param dto {@link MayoristaRequestDTO} que contiene los nuevos datos del mayorista
     *            (nombre, apellido, dirección, teléfono, fecha de cumpleaños)
     *
     * @return {@link MayoristaResponseDTO} con los datos del mayorista actualizado
     *
     * @throws RecursoNoEncontradoException Si no existe un mayorista con el ID proporcionado
     * @throws IllegalArgumentException Si el DTO contiene datos inválidos
     *
     * @see MayoristaRequestDTO
     * @see MayoristaResponseDTO
     * @see RecursoNoEncontradoException
     */
    @Override
    public MayoristaResponseDTO modificarMayorista(Integer mayoristaId, MayoristaRequestDTO dto) {

        Mayorista mayorista = mayoristaRepository.findById(mayoristaId)
                .orElseThrow(()-> new RecursoNoEncontradoException("Mayorista no encontrado"));

        mayorista.setNombre(dto.getNombre());
        mayorista.setApellido(dto.getApellido());
        mayorista.setDireccion(dto.getDireccion());
        mayorista.setTelefono(dto.getTelefono());
        mayorista.setFechaCumpleanos(dto.getFechaCumpleanos());

        mayoristaRepository.save(mayorista);

        return MayoristaResponseDTO.builder()
                .nombre(mayorista.getNombre())
                .apellido(mayorista.getApellido())
                .direccion(mayorista.getDireccion())
                .telefono(mayorista.getTelefono())
                .fechaCumpleanos(mayorista.getFechaCumpleanos())
                .build();
    }

}

package com.habanaCrem.gestionProductos.Service.Impl;

import com.habanaCrem.gestionProductos.DTOs.GastoRequestDTO;
import com.habanaCrem.gestionProductos.DTOs.GastoResponseDTO;
import com.habanaCrem.gestionProductos.Entity.Gasto;
import com.habanaCrem.gestionProductos.Exception.NegocioException;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.GastoRepository;
import com.habanaCrem.gestionProductos.Repository.UsuarioRepository;
import com.habanaCrem.gestionProductos.Service.CajaService;
import com.habanaCrem.gestionProductos.Service.GastoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GastoServiceimpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaService cajaService;

    @Override
    public GastoResponseDTO registrarGasto(
            GastoRequestDTO dto, Integer usuarioId)
    {

        cajaService.validarCajaAbiertaHoy();

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Gasto gasto = Gasto.builder()
                .fecha(LocalDate.now())
                .monto(dto.getMonto())
                .descripcion(dto.getDescripcion())
                .tipoGasto(dto.getTipoGasto())
                .usuario(usuarioRepository.getReferenceById(usuarioId))
                .build();

        gastoRepository.save(gasto);

        return GastoResponseDTO.builder()
                .id(gasto.getId())
                .fecha(gasto.getFecha())
                .monto(gasto.getMonto())
                .descripcion(gasto.getDescripcion())
                .tipoGasto(gasto.getTipoGasto())
                .rolUsuario(gasto.getUsuario().getRol())
                .build();
    }

    @Override
    public List<GastoResponseDTO> obtenerGastosPorRango(
            LocalDate inicio, LocalDate fin)
    {

        if(inicio.isAfter(fin)) {
            throw new NegocioException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        List<Gasto> gastos = gastoRepository.findByFechaBetween(inicio, fin);

        if(gastos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron gastos en el rango de fechas especificado");
        }

        return gastos.stream()
                .map(gasto -> GastoResponseDTO.builder()
                        .id(gasto.getId())
                        .fecha(gasto.getFecha())
                        .monto(gasto.getMonto())
                        .descripcion(gasto.getDescripcion())
                        .tipoGasto(gasto.getTipoGasto())
                        .rolUsuario(gasto.getUsuario().getRol())
                        .build())
                .toList();
    }

    @Override
    public GastoResponseDTO modificarGasto(
            Integer gastoId,
            GastoRequestDTO dto,
            Integer usuarioId)
    {

        cajaService.validarCajaAbiertaHoy();

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Gasto no encontrado"));

        gasto.setMonto(dto.getMonto());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setTipoGasto(dto.getTipoGasto());
        gastoRepository.save(gasto);

        return GastoResponseDTO.builder()
                .id(gasto.getId())
                .fecha(gasto.getFecha())
                .monto(gasto.getMonto())
                .descripcion(gasto.getDescripcion())
                .tipoGasto(gasto.getTipoGasto())
                .rolUsuario(gasto.getUsuario().getRol())
                .build();
    }

    @Override
    public void eliminarGasto(Integer gastoId) {

        cajaService.validarCajaAbiertaHoy();

        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Gasto no encontrado"));

        gastoRepository.delete(gasto);

    }
}

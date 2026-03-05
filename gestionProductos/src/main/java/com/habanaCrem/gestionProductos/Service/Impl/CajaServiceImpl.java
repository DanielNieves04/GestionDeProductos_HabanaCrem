package com.habanaCrem.gestionProductos.Service.Impl;

import com.habanaCrem.gestionProductos.DTOs.CajaCierreResponeDTO;
import com.habanaCrem.gestionProductos.DTOs.CajaResponseDTO;
import com.habanaCrem.gestionProductos.Entity.*;
import com.habanaCrem.gestionProductos.Exception.NegocioException;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.CajaRepository;
import com.habanaCrem.gestionProductos.Repository.GastoRepository;
import com.habanaCrem.gestionProductos.Repository.UsuarioRepository;
import com.habanaCrem.gestionProductos.Repository.VentaRepository;
import com.habanaCrem.gestionProductos.Service.CajaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final GastoRepository gastoRepository;

    @Transactional
    public CajaResponseDTO abrirCaja(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario).
                orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (usuario.getRol() != Rol.Administrador &&
                usuario.getRol() != Rol.GestorPuntoDeVenta)
        {
            throw new NegocioException("Usuario no tiene permisos para abrir caja");
        }

        LocalDate hoy = LocalDate.now();

        if(cajaRepository.existsByFecha(hoy)) {
            throw new NegocioException("Ya existe una caja abierta para hoy");
        }

        Caja caja = Caja.builder()
                .fecha(hoy)
                .estado(EstadoCaja.ABIERTA)
                .fechaHoraApertura(LocalDateTime.now())
                .usuarioApertura(usuario)
                .totalVentas(BigDecimal.ZERO)
                .totalEfectivo(BigDecimal.ZERO)
                .totalGastos(BigDecimal.ZERO)
                .totalTransferencias(BigDecimal.ZERO)
                .utilidadDia(BigDecimal.ZERO)
                .build();

        cajaRepository.save(caja);

        return CajaResponseDTO.builder()
                .idCaja(caja.getId())
                .fechaApertura(caja.getFecha())
                .estado(caja.getEstado())
                .build();
    }

    @Transactional
    public CajaCierreResponeDTO cerrarCaja(Integer idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario).
                orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        LocalDate hoy = LocalDate.now();

        Caja caja = cajaRepository.findByFecha(hoy)
                .orElseThrow(() -> new NegocioException("No hay una caja abierta para hoy"));


        if(caja.getEstado() == EstadoCaja.CERRADA) {
            throw new NegocioException("La caja ya está cerrada");
        }

        List<Venta> ventas = ventaRepository.findByFecha(hoy);
        List<Gasto> gastos = gastoRepository.findByFecha(hoy);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;
        BigDecimal totalTransferencias = BigDecimal.ZERO;

        for(Venta venta : ventas) {
            totalVentas = totalVentas.add(venta.getTotal());

            if(venta.getTransferenciaTotal() != null) {
                totalTransferencias = totalTransferencias.add(venta.getTransferenciaTotal());
            }
        }

        for(Gasto gasto : gastos) {
            totalGastos = totalGastos.add(gasto.getMonto());
        }

        BigDecimal totalEfectivo = totalVentas.subtract(totalTransferencias);
        BigDecimal utilidadDia = totalVentas.subtract(totalGastos);

        caja.setTotalVentas(totalVentas);
        caja.setTotalTransferencias(totalTransferencias);
        caja.setTotalEfectivo(totalEfectivo);
        caja.setTotalGastos(totalGastos);
        caja.setUtilidadDia(utilidadDia);

        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaHoraCierre(LocalDateTime.now());
        caja.setUsuarioCierre(usuario);

        cajaRepository.save(caja);

        return CajaCierreResponeDTO.builder()
                .fecha(caja.getFecha())
                .totalVentas(caja.getTotalVentas())
                .totalEfectivo(caja.getTotalEfectivo())
                .totalTransferencias(caja.getTotalTransferencias())
                .totalGastos(caja.getTotalGastos())
                .utilidadDia(caja.getUtilidadDia())
                .build();
    }

    public Caja validarCajaAbiertaHoy() {

        LocalDate hoy = LocalDate.now();

        Caja caja = cajaRepository.findByFecha(hoy)
                .orElseThrow(() ->
                        new NegocioException("Debe abrir caja antes de operar")
                );

        if (caja.getEstado() == EstadoCaja.CERRADA) {
            throw new NegocioException("La caja del día está cerrada");
        }

        return caja;
    }
}

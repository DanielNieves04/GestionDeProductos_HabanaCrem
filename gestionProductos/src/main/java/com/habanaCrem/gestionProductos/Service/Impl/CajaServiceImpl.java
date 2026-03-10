package com.habanaCrem.gestionProductos.Service.Impl;

import com.habanaCrem.gestionProductos.DTOs.*;
import com.habanaCrem.gestionProductos.Entity.*;
import com.habanaCrem.gestionProductos.Exception.NegocioException;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.*;
import com.habanaCrem.gestionProductos.Service.CajaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final GastoRepository gastoRepository;
    private final DetallesVentaRepository detalleVentaRepository;

    @Transactional
    public List<VentasDiariasDTO> obtenerVentasDiariasPorRango(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new NegocioException("La fecha inicial no puede ser mayor a la final");
        }

        List<Venta> ventas = ventaRepository.
                findByFechaBetween(fechaInicio, fechaFin);

        List<Caja> cajas = cajaRepository.
                findByFechaBetween(fechaInicio, fechaFin);

        List<VentasDiariasDTO> ventasDiarias = new ArrayList<>();
        for (Caja caja : cajas) {

            LocalDate fecha = caja.getFecha();

            BigDecimal totalLocal = BigDecimal.ZERO;
            BigDecimal totalMayorista = BigDecimal.ZERO;
            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalTransferencias = BigDecimal.ZERO;

            for (Venta venta : ventas) {

                if (venta.getFecha().equals(fecha)) {

                    totalVentas = totalVentas.add(venta.getTotal());

                    BigDecimal transferencia = venta.getTransferenciaTotal() != null
                            ? venta.getTransferenciaTotal()
                            : BigDecimal.ZERO;

                    totalTransferencias = totalTransferencias.add(transferencia);

                    if (venta.getTipoVenta() == TipoVenta.LOCAL) {
                        totalLocal = totalLocal.add(venta.getTotal());
                    } else {
                        totalMayorista = totalMayorista.add(venta.getTotal());
                    }
                }
            }

            BigDecimal totalEfectivo = totalVentas.subtract(totalTransferencias);

            BigDecimal totalGastos = caja.getTotalGastos() != null
                    ? caja.getTotalGastos()
                    : BigDecimal.ZERO;

            BigDecimal utilidadTotal = totalVentas.subtract(totalGastos);

            ventasDiarias.add(
                    VentasDiariasDTO.builder()
                            .fecha(fecha)
                            .totalLocal(totalLocal)
                            .totalMayorista(totalMayorista)
                            .totalVentas(totalVentas)
                            .totalEfectivo(totalEfectivo)
                            .totalTransferencias(totalTransferencias)
                            .totalGastos(totalGastos)
                            .utilidadDia(utilidadTotal)
                            .build()
            );
        }

        return ventasDiarias;
    }


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

    @Transactional
    public ReporteFinancieroDTO generarReporte(
            LocalDate inicio,
            LocalDate fin
    ) {

        if (inicio.isAfter(fin)) {
            throw new NegocioException("La fecha inicial no puede ser mayor a la final");
        }

        List<Caja> cajas = cajaRepository
                .findByFechaBetweenAndEstado(inicio, fin, EstadoCaja.CERRADA);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTransferencias = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;
        BigDecimal utilidadTotal = BigDecimal.ZERO;

        List<CajaResumenDTO> resumenes = new ArrayList<>();

        for (Caja caja : cajas) {

            totalVentas = totalVentas.add(caja.getTotalVentas());
            totalEfectivo = totalEfectivo.add(caja.getTotalEfectivo());
            totalTransferencias = totalTransferencias.add(caja.getTotalTransferencias());
            totalGastos = totalGastos.add(caja.getTotalGastos());
            utilidadTotal = utilidadTotal.add(caja.getUtilidadDia());

            resumenes.add(
                    CajaResumenDTO.builder()
                            .fecha(caja.getFecha())
                            .totalVentas(caja.getTotalVentas())
                            .utilidadDia(caja.getUtilidadDia())
                            .build()
            );
        }

        return ReporteFinancieroDTO.builder()
                .fechaInicio(inicio)
                .fechaFin(fin)
                .totalVentas(totalVentas)
                .totalEfectivo(totalEfectivo)
                .totalTransferencias(totalTransferencias)
                .totalGastos(totalGastos)
                .utilidadTotal(utilidadTotal)
                .cajas(resumenes)
                .build();
    }

    @Transactional
    public ReporteTipoVentaDTO reporteTipoDeVenta(
            LocalDate inicio,
            LocalDate fin
    ) {

        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);

        BigDecimal totalLocal = BigDecimal.ZERO;
        BigDecimal totalMayorista = BigDecimal.ZERO;

        BigDecimal transferenciasLocal = BigDecimal.ZERO;
        BigDecimal transferenciasMayorista = BigDecimal.ZERO;

        for(Venta venta: ventas) {

            BigDecimal transferencia = venta.getTransferenciaTotal() != null
                    ? venta.getTransferenciaTotal()
                    : BigDecimal.ZERO;

            if(venta.getTipoVenta() == TipoVenta.LOCAL) {

                totalLocal = totalLocal.add(venta.getTotal());
                transferenciasLocal = transferenciasLocal.add(transferencia);

            } else { //MAYORISTA

                totalMayorista = totalMayorista.add(venta.getTotal());
                transferenciasMayorista = transferenciasMayorista.add(transferencia);

            }
        }

        BigDecimal efectivoLocal = totalLocal.subtract(transferenciasLocal);
        BigDecimal efectivoMayorista = totalMayorista.subtract(transferenciasMayorista);

        return ReporteTipoVentaDTO.builder()
                .totalLocal(totalLocal)
                .totalMayorista(totalMayorista)
                .efectivoLocal(efectivoLocal)
                .efectivoMayorista(efectivoMayorista)
                .transferenciasLocal(transferenciasLocal)
                .transferenciasMayorista(transferenciasMayorista)
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

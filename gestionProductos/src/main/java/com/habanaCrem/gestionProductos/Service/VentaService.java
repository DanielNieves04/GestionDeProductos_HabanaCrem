package com.habanaCrem.gestionProductos.Service;

import com.habanaCrem.gestionProductos.DTOs.*;
import com.habanaCrem.gestionProductos.Entity.*;
import com.habanaCrem.gestionProductos.Exception.NegocioException;
import com.habanaCrem.gestionProductos.Exception.RecursoNoEncontradoException;
import com.habanaCrem.gestionProductos.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de la gestión de ventas del sistema.
 *
 * Reglas de negocio principales:
 * - Toda venta es transaccional.
 * - El inventario se descuenta automáticamente al registrar una venta.
 * - Al eliminar una venta, el stock se revierte.
 * - No se permite stock negativo.
 * - En ventas LOCAL se permiten unidades defectuosas y no existen devoluciones.
 * - En ventas MAYORISTA existen devoluciones y no se permiten unidades defectuosas.
 * - El inventario solo se descuenta por unidades efectivamente vendidas
 *   (y defectuosas en LOCAL).

 * Este servicio centraliza la lógica crítica del módulo de ventas
 * para garantizar la integridad de inventario y datos financieros.
 */

@Service
@Transactional
@RequiredArgsConstructor
public class VentaService{

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final MayoristaRepository mayoristaRepository;
    private final InventarioRepository inventarioRepository;
    private final CajaService cajaService;

    /**
     * Registra una nueva venta en el sistema.
     *
     * Proceso:
     * 1. Valida la existencia del usuario.
     * 2. Valida la disponibilidad de inventario por producto.
     * 3. Descuenta el stock correspondiente (vendidas).
     * 4. Calcula totales y ganancias.
     * 5. Guarda la venta y sus detalles en una sola transacción.
     *
     * @param dto datos de la venta a registrar
     * @param usuarioId identificador del usuario que realiza la venta
     * @return información resumida de la venta registrada
     * @throws RecursoNoEncontradoException si el usuario o producto no existe
     * @throws NegocioException si no hay stock suficiente
     */
    @Transactional
    public VentaResponseDTO registrarVenta(
            VentaRequestDTO dto,
            Integer usuarioId) {

        cajaService.validarCajaAbiertaHoy();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setTipoVenta(dto.getTipoVenta());
        venta.setUsuario(usuario);
        venta.setTransferenciaTotal(dto.getTransferenciaTotal());

        if(dto.getMayoristaId() != null){
            Mayorista mayorista = mayoristaRepository.findById(dto.getMayoristaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("mayorista no encontrado"));
            venta.setMayorista(mayorista);
        }

        BigDecimal totalVenta = BigDecimal.ZERO;

        for(DetalleVentaRequestDTO det : dto.getDetalles()){

            if (det.getCantidadEntregada() <= 0) {
                throw new NegocioException(
                        "La cantidad vendida debe ser mayor a cero"
                );
            }

            Producto producto = productoRepository.findById(det.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            Inventario inventario = null;
            for(Inventario inv : producto.getInventarios()){
                if(inv.getBodega().getTipoBodega() == TipoBodega.PUNTO_VENTA){
                    inventario = inventarioRepository
                            .findByProductoIdAndBodegaId(producto.getId(), inv.getBodega().getId())
                            .orElseThrow(() -> new NegocioException(
                                    "No existe inventario para " + producto.getNombre()
                            ));
                }
            }

            validarDetallePorTipoVenta(venta.getTipoVenta(),det,producto);

            /**
             * En ventas MAYORISTAS el inventario se descuenta de
             * las unidades entregadas, luego cuando hay unidades devueltas
             * al modificar la información el inventario descuenta las unidades
             * entregadas menos las devueltas.
             */

            int unidadesADescontar = 0;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {
                unidadesADescontar =
                        det.getCantidadEntregada() + det.getDefectuosos();
            }else{ //SI ES MAYORISTA
                unidadesADescontar = det.getCantidadEntregada() -
                        (det.getDevoluciones() != null ? det.getDevoluciones():0);
            }

            assert inventario != null;
            if (inventario.getStockActual() < unidadesADescontar) {
                throw new NegocioException("Stock insuficiente para " + producto.getNombre());
            }

            inventario.setStockActual(
                    inventario.getStockActual() - unidadesADescontar
            );

            BigDecimal subtotal;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {

                subtotal = producto.getPrecioLocal()
                        .multiply(BigDecimal.valueOf(det.getCantidadEntregada()));

                if (det.getDefectuosos() > 0) {
                    subtotal = subtotal.add(
                            producto.getPrecioDefectuoso()
                                    .multiply(BigDecimal.valueOf(det.getDefectuosos()))
                    );
                }

            } else { // MAYORISTA

                subtotal = producto.getPrecioMayorista()
                        .multiply(BigDecimal.valueOf(unidadesADescontar));
            }

            DetallesVenta detallesVenta = new DetallesVenta();
            detallesVenta.setVenta(venta);
            detallesVenta.setProducto(producto);
            detallesVenta.setCantidadEntregada(det.getCantidadEntregada());
            detallesVenta.setCantidadDefectuosos(det.getDefectuosos());
            detallesVenta.setCantidadDevuelta(det.getDevoluciones());
            detallesVenta.setTotal(subtotal);

            if (venta.getTipoVenta() == TipoVenta.MAYORISTA) {
                BigDecimal ganancia = producto.getGananciaMayorista()
                        .multiply(BigDecimal.valueOf(det.getCantidadEntregada()-
                                (det.getDevoluciones() != null ? det.getDevoluciones():0)));
                detallesVenta.setGananciaMayorista(ganancia);
            }

            venta.getDetallesVentas().add(detallesVenta);

            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        ventaRepository.save(venta);

        return VentaResponseDTO.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .total(venta.getTotal())
                .tipoVenta(venta.getTipoVenta())
                .build();
    }

    /**
     * Obtener ventas por rango de fecha en el sistema.
     *
     * Proceso:
     * 1. Válida que la fecha inicio no sea mayor a la fecha fin.
     * 2. Busca las ventas desde la fecha inicio a la fecha fin.
     * 3. Válida que exista la venta.
     *
     * @param inicio
     * @param fin
     * @return muestra todas las ventas encontradas en ese rango de fecha
     * @throws NegocioException si la inicio fin es mayor a la final
     */
    @Transactional
    public List<VentaResponseDTO> obtenerVentasPorRango(
            LocalDate inicio,
            LocalDate fin
    ){
        if(inicio.isAfter(fin)){
            throw new NegocioException("La fecha inicial no puede ser mayor a la final");
        }

        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio,fin);

        if (ventas.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen ventas en este rango de fechas");
        }

        return ventas.stream()
                .map(venta -> VentaResponseDTO.builder()
                        .id(venta.getId())
                        .fecha(venta.getFecha())
                        .total(venta.getTotal())
                        .tipoVenta(venta.getTipoVenta())
                        .nombreMayorista(venta.getMayorista() != null
                                ? venta.getMayorista().getNombre()
                                : null)
                        .detallesVenta(
                                venta.getDetallesVentas()
                                        .stream()
                                        .map(detalle -> DetalleVentaResponseDTO.builder()
                                                .id(detalle.getId())
                                                .productoId(detalle.getProducto().getId())
                                                .nombreProducto(detalle.getProducto().getNombre())
                                                .cantidadEntregada(detalle.getCantidadEntregada())
                                                .cantidadDefectuosos(detalle.getCantidadDefectuosos())
                                                .cantidadDevuelta(detalle.getCantidadDevuelta())
                                                .subtotal(detalle.getTotal())
                                                .gananciaMayorista(detalle.getGananciaMayorista())
                                                .build()
                                        ).toList()
                        )
                        .build())
                .toList();
    }

    /**
     * Obtener venta por fecha y mayorista en el sistema.
     *
     * Proceso:
     * 1. Valida que la fecha no se mayor a la fecha actual.
     * 2. Busca las venta que este relacionada con el mayorista y la fecha.
     * 3. Valida que exista la venta.
     *
     * @param fecha determina la fecha de creacion de la venta a obtener
     * @param mayoristaId identificador del mayorista relaionado a la venta
     * @return muestra toda la venta registrada en la fecha y que esta relacionada al mayorista
     * @throws NegocioException si la fecha excede la fecha actual
     * @throws RecursoNoEncontradoException si no se encuentra la venta o el mayorista
     */
    @Transactional
    public VentaResponseDTO obtenerVentaPorFechaYMayorista(
            LocalDate fecha,
            Integer mayoristaId
    ){

        Mayorista mayorista = mayoristaRepository.findById(mayoristaId)
                .orElseThrow(()->new RecursoNoEncontradoException("El mayoista no existe"));

        if(fecha.isAfter(LocalDate.now())){
            throw new NegocioException("La fecha no puede ser mayor a la actual");
        }

        Venta venta = ventaRepository.findByFechaAndMayorista_Id(fecha,mayorista.getId())
                .orElseThrow(()-> new RecursoNoEncontradoException("Venta no encontrada"));

        return VentaResponseDTO.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .total(venta.getTotal())
                .tipoVenta(venta.getTipoVenta())
                .nombreMayorista(venta.getMayorista().getNombre())
                .detallesVenta(
                        venta.getDetallesVentas()
                                .stream()
                                .map(detalle -> DetalleVentaResponseDTO.builder()
                                        .id(detalle.getId())
                                        .productoId(detalle.getProducto().getId())
                                        .nombreProducto(detalle.getProducto().getNombre())
                                        .cantidadEntregada(detalle.getCantidadEntregada())
                                        .cantidadDefectuosos(detalle.getCantidadDefectuosos())
                                        .cantidadDevuelta(detalle.getCantidadDevuelta())
                                        .subtotal(detalle.getTotal())
                                        .gananciaMayorista(detalle.getGananciaMayorista())
                                        .build()
                                ).toList()
                )
                .build();
    }

    /**
     * Obtener ventas por fecha y tipo de venta.
     *
     * Proceso:
     * 1. Válida que la fecha no sea mayor a la fecha actual.
     * 2. Busca la venta que esté relacionada con la fecha y el tipo de venta.
     * 3. Válida que exista la venta.
     *
     * @param fecha determina la fecha de creacion de la venta a obtener
     * @param tipoVenta valor de el tipo de venta a obtener
     * @return muestra todas las ventas registradas en la fecha y que coincidan con el tipo de venta.
     * @throws NegocioException si la fecha excede la fecha actual
     * @throws RecursoNoEncontradoException si no se encuentra la venta
     */
    @Transactional
    public List<VentaResponseDTO> obtenerVentaPorFechaYTipoventa(
            LocalDate fecha,
            TipoVenta tipoVenta
    ){
        if(fecha.isAfter(LocalDate.now())){
            throw new NegocioException("La fecha no puede ser mayor a la actual");
        }

        List<Venta> ventas = ventaRepository.findByFechaAndTipoVenta(fecha,tipoVenta);

        if (ventas.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen ventas para esta fecha o tipo de venta");
        }

        return ventas.stream()
                .map(venta -> VentaResponseDTO.builder()
                        .id(venta.getId())
                        .fecha(venta.getFecha())
                        .total(venta.getTotal())
                        .tipoVenta(venta.getTipoVenta())
                        .nombreMayorista(venta.getMayorista().getNombre())
                        .detallesVenta(
                                venta.getDetallesVentas()
                                        .stream()
                                        .map(detalle -> DetalleVentaResponseDTO.builder()
                                                .id(detalle.getId())
                                                .productoId(detalle.getProducto().getId())
                                                .nombreProducto(detalle.getProducto().getNombre())
                                                .cantidadEntregada(detalle.getCantidadEntregada())
                                                .cantidadDefectuosos(detalle.getCantidadDefectuosos())
                                                .cantidadDevuelta(detalle.getCantidadDevuelta())
                                                .subtotal(detalle.getTotal())
                                                .gananciaMayorista(detalle.getGananciaMayorista())

                                                .build()
                                        ).toList()
                        )
                        .build())
                .toList();
    }

    /**
     * Eliminar una venta en el sistema.
     *
     * Proceso:
     * 1. Valida la existencia de la venta.
     * 2. Valida la disponibilidad de inventario por producto.
     * 3. Devuelve el stock correspondiente (vendidas).
     * 5. Elimnia la venta.
     *
     * @param ventaId identificador de la venta a eliminar
     * @throws RecursoNoEncontradoException si la venta o producto no existe
     * @throws NegocioException si el inventario no es encontrado
     */
    @Transactional
    public void eliminarVenta(Integer ventaId){

        cajaService.validarCajaAbiertaHoy();

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(()-> new RecursoNoEncontradoException("No se encontro la venta"));

        for(DetallesVenta detalle : venta.getDetallesVentas()){

            Producto producto = detalle.getProducto();

            Inventario inventario = null;
            for(Inventario inv : producto.getInventarios()){
                if(inv.getBodega().getTipoBodega() == TipoBodega.PUNTO_VENTA){
                    inventario = inventarioRepository
                            .findByProductoIdAndBodegaId(producto.getId(), inv.getBodega().getId())
                            .orElseThrow(() -> new NegocioException(
                                    "No existe inventario para " + producto.getNombre()
                            ));
                }
            }

            int unidadesAInvertir;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {
                unidadesAInvertir = detalle.getCantidadEntregada() +
                        (detalle.getCantidadDefectuosos() != null ? detalle.getCantidadDefectuosos() : 0);
            }else { //MAYORISTA
                unidadesAInvertir = detalle.getCantidadEntregada() -
                        (detalle.getCantidadDevuelta() != null ? detalle.getCantidadDevuelta() : 0);
            }

            inventario.setStockActual(
                    inventario.getStockActual() + unidadesAInvertir
            );
        }

        ventaRepository.delete(venta);

    }

    /**
     * Modifica una venta existente en el sistema.
     *
     * Proceso:
     * 1. Recupera la venta original.
     * 2. Revierte el inventario asociado a la venta original.
     * 3. Elimina los detalles anteriores.
     * 4. Aplica los nuevos detalles de la venta.
     * 5. Recalcula totales y ganancias.
     *
     * Toda la operación es transaccional para garantizar
     * la consistencia del inventario y los datos financieros.
     *
     * @param ventaId identificador de la venta a modificar
     * @param dto nuevos datos de la venta
     * @return información resumida de la venta modificada
     * @throws RecursoNoEncontradoException si la venta o producto no existe
     * @throws NegocioException para excepciones de negocio
     */
    @Transactional
    public VentaResponseDTO modificarVenta(Integer ventaId, VentaRequestDTO dto){

        cajaService.validarCajaAbiertaHoy();

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(()-> new RecursoNoEncontradoException("Venta no encontrada"));

        for (DetallesVenta detalle : venta.getDetallesVentas()) {

            Inventario inventario = null;
            for(Inventario inv : detalle.getProducto().getInventarios()){
                if(inv.getBodega().getTipoBodega() == TipoBodega.PUNTO_VENTA){
                    inventario = inventarioRepository
                            .findByProductoIdAndBodegaId(detalle.getProducto().getId(), inv.getBodega().getId())
                            .orElseThrow(() -> new NegocioException(
                                    "No existe inventario para " + detalle.getProducto().getNombre()
                            ));
                }
            }

            int unidadesARevertir;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {
                unidadesARevertir = detalle.getCantidadEntregada() +
                        (detalle.getCantidadDefectuosos() != null ? detalle.getCantidadDefectuosos() : 0);
            }else { //MAYORISTA
                unidadesARevertir = detalle.getCantidadEntregada() -
                        (detalle.getCantidadDevuelta() != null ? detalle.getCantidadDevuelta() : 0);
            }

            assert inventario != null;
            inventario.setStockActual(inventario.getStockActual() + unidadesARevertir);
        }

        venta.getDetallesVentas().clear();

        venta.setTipoVenta(dto.getTipoVenta());
        venta.setTransferenciaTotal(dto.getTransferenciaTotal());

        if(dto.getMayoristaId() != null){
            Mayorista mayorista = mayoristaRepository.findById(dto.getMayoristaId())
                    .orElseThrow(()-> new RecursoNoEncontradoException("Mayorista no encontrado"));
            venta.setMayorista(mayorista);
        }else{
            venta.setMayorista(null);
        }

        BigDecimal totalVenta = BigDecimal.ZERO;

        for(DetalleVentaRequestDTO det : dto.getDetalles()){

            if(det.getCantidadEntregada() <= 0){
                throw new NegocioException("La cantidad vendida debe ser mayor a cero");
            }

            Producto producto = productoRepository.findById(det.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            Inventario inventario = null;
            for(Inventario inv : producto.getInventarios()){
                if(inv.getBodega().getTipoBodega() == TipoBodega.PUNTO_VENTA){
                    inventario = inventarioRepository
                            .findByProductoIdAndBodegaId(producto.getId(), inv.getBodega().getId())
                            .orElseThrow(() -> new NegocioException(
                                    "No existe inventario para " + producto.getNombre()
                            ));
                }
            }

            int unidadesADescontar = 0;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {
                unidadesADescontar =
                        det.getCantidadEntregada() +
                                (det.getDefectuosos() != null ? det.getDefectuosos():0);
            }else{ //SI ES MAYORISTA
                unidadesADescontar = det.getCantidadEntregada() -
                        (det.getDevoluciones() != null ? det.getDevoluciones():0);
            }

            if (inventario.getStockActual() < unidadesADescontar) {
                throw new NegocioException("Stock insuficiente para " + producto.getNombre());
            }

            inventario.setStockActual(inventario.getStockActual() - unidadesADescontar);

            BigDecimal subtotal;

            if (venta.getTipoVenta() == TipoVenta.LOCAL) {

                subtotal = producto.getPrecioLocal()
                        .multiply(BigDecimal.valueOf(det.getCantidadEntregada()));

                if (det.getDefectuosos() > 0) {
                    subtotal = subtotal.add(
                            producto.getPrecioDefectuoso()
                                    .multiply(BigDecimal.valueOf(det.getDefectuosos()))
                    );
                }

            } else { // MAYORISTA

                subtotal = producto.getPrecioMayorista()
                        .multiply(BigDecimal.valueOf(unidadesADescontar));
            }

            DetallesVenta nuevoDetalle = new DetallesVenta();
            nuevoDetalle.setVenta(venta);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidadEntregada(det.getCantidadEntregada());
            nuevoDetalle.setCantidadDefectuosos(det.getDefectuosos());
            nuevoDetalle.setCantidadDevuelta(det.getDevoluciones());
            nuevoDetalle.setTotal(subtotal);

            if (venta.getTipoVenta() == TipoVenta.MAYORISTA) {
                BigDecimal ganancia = producto.getGananciaMayorista()
                        .multiply(BigDecimal.valueOf(det.getCantidadEntregada()-
                                (det.getDevoluciones() != null ? det.getDevoluciones():0)));
                nuevoDetalle.setGananciaMayorista(ganancia);
            }

            venta.getDetallesVentas().add(nuevoDetalle);
            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);

        Venta ventaActualizada = ventaRepository.save(venta);

        return VentaResponseDTO.builder()
                .id(ventaActualizada.getId())
                .fecha(ventaActualizada.getFecha())
                .total(ventaActualizada.getTotal())
                .tipoVenta(ventaActualizada.getTipoVenta())
                .build();

    }

    private void validarDetallePorTipoVenta(
            TipoVenta tipoVenta,
            DetalleVentaRequestDTO det,
            Producto producto
    ) {
        if (tipoVenta == TipoVenta.LOCAL) {

            if (det.getDevoluciones() != 0) {
                throw new NegocioException(
                        "No se permiten devoluciones en venta LOCAL para "
                                + producto.getNombre()
                );
            }

        } else { // MAYORISTA

            if (det.getDefectuosos() != 0) {
                throw new NegocioException(
                        "No se permiten defectuosos en venta MAYORISTA para "
                                + producto.getNombre()
                );
            }
        }
    }
}

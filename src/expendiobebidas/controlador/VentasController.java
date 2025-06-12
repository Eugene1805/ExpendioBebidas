package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.dao.PromocionDAO;
import expendiobebidas.modelo.dao.VentaDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.modelo.pojo.DetalleVenta;
import expendiobebidas.modelo.pojo.Promocion;
import expendiobebidas.modelo.pojo.Venta;
import expendiobebidas.vista.Ventas;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author meler
 */
public class VentasController {

    private final Ventas vista;
    private final VentaDAO ventaDAO;
    private final BebidaDAO bebidaDAO;
    private final ClienteDAO clienteDAO;
    private final PromocionDAO promocionDAO;

    private List<Bebida> bebidas;
    private List<Cliente> clientes;
    private List<DetalleVenta> detallesVenta;
    
    private Cliente clienteBloqueado = null;
    private BigDecimal totalVenta = BigDecimal.ZERO;
    private LocalDate fechaVentaActual; // <-- CAMBIO CLAVE: Almacenar la fecha actual

    // CAMBIO CLAVE: Formateador solo para fecha
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public VentasController(Ventas vista) {
        this.vista = vista;
        this.ventaDAO = new VentaDAO();
        this.bebidaDAO = new BebidaDAO();
        this.clienteDAO = new ClienteDAO();
        this.promocionDAO = new PromocionDAO();        
        
        this.bebidas = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.detallesVenta = new ArrayList<>();

        configurarListeners();
        cargarDatosIniciales();
        inicializarVista();
    }

    private void configurarListeners() {
        vista.getBtnAgregarProducto().addActionListener(e -> agregarBebidaATabla());
        vista.getBtnGenerarVenta().addActionListener(e -> finalizarVenta());
        vista.getBtnCancelar().addActionListener(e -> cancelarVenta());
        vista.getSpnDescuentoVenta().addChangeListener(e -> actualizarTotalConDescuentoGeneral());
    }
    
    private void cargarDatosIniciales() {
        try {
            bebidas = BebidaDAO.readAll();
            DefaultComboBoxModel<Bebida> bebidaModel = new DefaultComboBoxModel<>();
            bebidas.forEach(bebidaModel::addElement);
            vista.getCbBebidaVenta().setModel(bebidaModel);

            clientes = ClienteDAO.readAll();
            DefaultComboBoxModel<Cliente> clienteModel = new DefaultComboBoxModel<>();
            clientes.forEach(clienteModel::addElement);
            vista.getCbClienteVenta().setModel(clienteModel);
        } catch (SQLException ex) {
            mostrarError("Error al cargar datos iniciales: " + ex.getMessage());
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void inicializarVista() {
        // CAMBIO CLAVE: Usar LocalDate
        fechaVentaActual = LocalDate.now();
        vista.getTfFechaVenta().setText(fechaVentaActual.format(DATE_FORMATTER));

        vista.getSpCantidad().setValue(1);
        vista.getSpnDescuentoVenta().setValue(0);
        vista.getLbTotalVenta().setText("0.00");
        vista.getTfFolioVenta().setText(generarFolio());
        vista.getCbClienteVenta().setEnabled(true);
        
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidaVenta().getModel();
        model.setRowCount(0);
    }

    private void agregarBebidaATabla() {
        Bebida bebidaSeleccionada = (Bebida) vista.getCbBebidaVenta().getSelectedItem();
        if (bebidaSeleccionada == null) {
            mostrarError("Debe seleccionar una bebida.");
            return;
        }

        int cantidad = (int) vista.getSpCantidad().getValue();
        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor a cero.");
            return;
        }

        if (cantidad > bebidaSeleccionada.getStockActual()) {
            mostrarError("No hay suficiente stock para '" + bebidaSeleccionada.getNombre() + "'.\nStock disponible: " + bebidaSeleccionada.getStockActual());
            return;
        }
        
        if (clienteBloqueado == null) {
            clienteBloqueado = (Cliente) vista.getCbClienteVenta().getSelectedItem();
            if (clienteBloqueado == null) {
                mostrarError("Debe seleccionar un cliente.");
                return;
            }
            vista.getCbClienteVenta().setEnabled(false);
        }
        // --- LÓGICA DE PROMOCIONES ---
        Promocion mejorPromocion = null;
        try {
            // 1. Obtener todas las promociones para esta bebida
            List<Promocion> promociones = PromocionDAO.obtenerPromocionesPorBebida(bebidaSeleccionada.getIdBebida());

            // 2. Filtrar las que están activas hoy y encontrar la de mayor descuento
            Optional<Promocion> promocionActiva = promociones.stream()
                .filter(p -> !fechaVentaActual.isBefore(p.getFechaInicio()) && !fechaVentaActual.isAfter(p.getFechaFin()))
                .max(Comparator.comparing(Promocion::getDescuento));

            if (promocionActiva.isPresent()) {
                mejorPromocion = promocionActiva.get();
            }

        } catch (SQLException ex) {
            mostrarError("Error al consultar promociones: " + ex.getMessage());
        }

        // --- CÁLCULO DE PRECIO Y SUBTOTAL ---
        BigDecimal precioOriginal = bebidaSeleccionada.getPrecio();
        BigDecimal precioConDescuento = precioOriginal;

        if (mejorPromocion != null) {
            BigDecimal porcentajeDescuento = mejorPromocion.getDescuento();
            BigDecimal montoDescuento = precioOriginal.multiply(porcentajeDescuento).divide(new BigDecimal(100));
            precioConDescuento = precioOriginal.subtract(montoDescuento);
        }

        BigDecimal subtotal = precioConDescuento.multiply(new BigDecimal(cantidad));

        // --- CREACIÓN DEL DETALLE DE VENTA ---
        DetalleVenta detalle = new DetalleVenta();
        detalle.setBebida(bebidaSeleccionada);
        detalle.setCantidad(cantidad);
        detalle.setPrecioMomento(precioOriginal); // Guardamos el precio original por si acaso
        detalle.setSubtotal(subtotal.doubleValue());
        detalle.setPromocionAplicada(mejorPromocion); // Guardamos la promoción
        
        detallesVenta.add(detalle);
        actualizarTablaYTotal();
    }

    private void cancelarVenta() {
        detallesVenta.clear();
        clienteBloqueado = null;
        totalVenta = BigDecimal.ZERO;
        
        inicializarVista();
        actualizarTablaYTotal();
    }

    private String generarFolio() {
        try {
            int anioActual = Year.now().getValue() % 100;
            int siguienteNumero = ventaDAO.obtenerUltimoNumeroFolio(anioActual) + 1;
            return String.format("VENT-%02d-%03d", anioActual, siguienteNumero);
        } catch (SQLException ex) {
            mostrarError("Error al generar el folio: " + ex.getMessage());
            return "VENT-ERR-000";
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void actualizarTablaYTotal() {
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidaVenta().getModel();
        model.setRowCount(0);

        totalVenta = BigDecimal.ZERO; // Este es el total ANTES del descuento general

        for (DetalleVenta detalle : detallesVenta) {
            Bebida bebida = detalle.getBebida();
            // El subtotal ya tiene el descuento de la promoción individual aplicado
            BigDecimal subtotal = new BigDecimal(detalle.getSubtotal()).setScale(2, RoundingMode.HALF_UP);
            totalVenta = totalVenta.add(subtotal);

            // Determinar qué mostrar en la columna de promoción
            String textoPromocion = "N/A";
            if (detalle.getPromocionAplicada() != null) {
                textoPromocion = String.format("%.0f%%", detalle.getPromocionAplicada().getDescuento());
            }

            Object[] row = {
                bebida.getNombre(),
                detalle.getCantidad(),
                bebida.getPrecio(), // Muestra el precio original
                subtotal, // Muestra el subtotal con la promoción ya aplicada
                textoPromocion // Muestra el descuento de la promoción
            };
            model.addRow(row);
        }

        // Una vez que la tabla y el totalVenta (sin descuento general) están listos,
        // llamamos a la función que actualiza la etiqueta del total final.
        actualizarTotalConDescuentoGeneral();
    }
     
    /**
     * Esta función se encarga únicamente de calcular y mostrar el total final
     * aplicando el descuento general del spinner. Se llama cada vez que la tabla
     * se actualiza o cuando el valor del spinner cambia.
     */
    private void actualizarTotalConDescuentoGeneral() {
        // Cálculo del descuento general (del spinner)
        int porcentajeDescuentoGeneral = (int) vista.getSpnDescuentoVenta().getValue();
        BigDecimal montoDescuentoGeneral = totalVenta.multiply(new BigDecimal(porcentajeDescuentoGeneral))
                                                     .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalFinalAPagar = totalVenta.subtract(montoDescuentoGeneral)
                                                .setScale(2, RoundingMode.HALF_UP);

        vista.getLbTotalVenta().setText(totalFinalAPagar.toString());
    }

    private void finalizarVenta() {
        if (detallesVenta.isEmpty() || clienteBloqueado == null) {
            mostrarError("No hay productos en la venta o no se ha seleccionado un cliente.");
            return;
        }

        try {
            // Calculamos el descuento y el total final de nuevo para asegurar consistencia
            int porcentajeDescuentoGeneral = (int) vista.getSpnDescuentoVenta().getValue();
            BigDecimal montoDescuentoGeneral = totalVenta.multiply(new BigDecimal(porcentajeDescuentoGeneral))
                                                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            BigDecimal totalFinalAPagar = totalVenta.subtract(montoDescuentoGeneral)
                                                    .setScale(2, RoundingMode.HALF_UP);

            Venta nuevaVenta = new Venta();
            nuevaVenta.setFecha(this.fechaVentaActual);
            nuevaVenta.setFolio(vista.getTfFolioVenta().getText());
            // Guardamos el MONTO del descuento general, no el porcentaje
            nuevaVenta.setDescuento(montoDescuentoGeneral); 
            nuevaVenta.setTotal(totalFinalAPagar);
            nuevaVenta.setCliente(clienteBloqueado);

            if (ventaDAO.registrarVentaCompleta(nuevaVenta, detallesVenta)) {
                JOptionPane.showMessageDialog(vista, 
                    "Venta registrada exitosamente con folio: " + nuevaVenta.getFolio(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cancelarVenta();
                
                // Actualizar stock en la vista de bebidas si se está usando en la sesión
                if (vista.getParent() instanceof java.awt.Container parent) {
                    for (java.awt.Component comp : parent.getComponents()) {
                        if (comp instanceof expendiobebidas.vista.Bebidas bebidasPanel) {
                            new BebidaController(bebidasPanel); // Esto recarga la tabla de bebidas
                            break;
                        }
                    }
                }
                
                // Actualizar pedidos a proveedor si está visible
                if (vista.getParent() instanceof java.awt.Container parent) {
                    for (java.awt.Component comp : parent.getComponents()) {
                        if (comp instanceof expendiobebidas.vista.PedidoProveedor pedidoPanel) {
                            new PedidoProveedorController(pedidoPanel).recargarTablaPedidosProveedor(); // ✅
                            break;
                        }
                    }
                }
                
                if(vista.getParent() instanceof java.awt.Container parent){
                    for(java.awt.Component comp : parent.getComponents()){
                        if(comp instanceof expendiobebidas.vista.Clientes clientesPanel){
                            new ClienteController(clientesPanel);
                        }
                    }
                }
            }

        } catch (SQLException ex) {
            mostrarError("Error en la base de datos al registrar la venta: " + ex.getMessage());
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, "Error en transacción de venta", ex);
        }
    }
}

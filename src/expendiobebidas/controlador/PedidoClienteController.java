package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.dao.PedidoClienteDAO; 
import expendiobebidas.modelo.dao.PromocionDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.modelo.pojo.OrdenPedido; 
import expendiobebidas.modelo.pojo.PedidoCliente; 
import expendiobebidas.modelo.pojo.Promocion;
import expendiobebidas.vista.PedidosClientes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author eugen
 */
public class PedidoClienteController implements ActionListener {

    private final PedidosClientes vista;
    private List<OrdenPedido> ordenesPedido;

    private Cliente clienteBloqueado = null;
    private BigDecimal totalBrutoPedido = BigDecimal.ZERO; // Total antes de descuento general
    private LocalDate fechaPedidoActual;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PedidoClienteController(PedidosClientes vista) {
        this.vista = vista;
        this.ordenesPedido = new ArrayList<>();

        configurarListeners();
        cargarDatosIniciales();
        inicializarVista();
    }

    private void configurarListeners() {
        vista.getBtnAgregarProducto().addActionListener(this);
        vista.getBtnGuardarPedido().addActionListener(this);
        vista.getBtnCancelar().addActionListener(this);
        vista.getSpnDescuento().addChangeListener(e -> actualizarTotalConDescuentoGeneral());
    }
    
    private void cargarDatosIniciales() {
        try {
            List<Bebida> bebidas = BebidaDAO.readAll();
            DefaultComboBoxModel<Bebida> bebidaModel = new DefaultComboBoxModel<>();
            bebidas.forEach(bebidaModel::addElement);
            vista.getCbBebidaVenta().setModel(bebidaModel);

            List<Cliente> clientes = ClienteDAO.readAll();
            DefaultComboBoxModel<Cliente> clienteModel = new DefaultComboBoxModel<>();
            clientes.forEach(clienteModel::addElement);
            vista.getCbClienteVenta().setModel(clienteModel);
        } catch (SQLException ex) {
            mostrarError("Error al cargar datos iniciales: " + ex.getMessage());
        }
    }

    private void inicializarVista() {
        fechaPedidoActual = LocalDate.now();
        vista.getTfFechaPedido().setText(fechaPedidoActual.format(DATE_FORMATTER));

        vista.getSpCantidad().setValue(1);
        vista.getSpnDescuento().setValue(0);
        vista.getLbTotalPedido().setText("0.00");
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
        
        // Bloquear cliente al agregar el primer producto
        if (clienteBloqueado == null) {
            clienteBloqueado = (Cliente) vista.getCbClienteVenta().getSelectedItem();
            if (clienteBloqueado == null) {
                mostrarError("Debe seleccionar un cliente.");
                return;
            }
            vista.getCbClienteVenta().setEnabled(false);
        }
        
        Promocion mejorPromocion = null;
        try {
            List<Promocion> promociones = PromocionDAO.obtenerPromocionesPorBebida(bebidaSeleccionada.getIdBebida());
            Optional<Promocion> promocionActiva = promociones.stream()
                .filter(p -> !fechaPedidoActual.isBefore(p.getFechaInicio()) && !fechaPedidoActual.isAfter(p.getFechaFin()))
                .max(Comparator.comparing(Promocion::getDescuento));
            if (promocionActiva.isPresent()) {
                mejorPromocion = promocionActiva.get();
            }
        } catch (SQLException ex) {
            mostrarError("Error al consultar promociones: " + ex.getMessage());
        }

        BigDecimal precioOriginal = bebidaSeleccionada.getPrecio();
        BigDecimal precioConDescuento = precioOriginal;

        if (mejorPromocion != null) {
            BigDecimal porcentajeDescuento = mejorPromocion.getDescuento();
            BigDecimal montoDescuento = precioOriginal.multiply(porcentajeDescuento).divide(new BigDecimal(100));
            precioConDescuento = precioOriginal.subtract(montoDescuento);
        }

        BigDecimal subtotal = precioConDescuento.multiply(new BigDecimal(cantidad));

        // Crear la orden de pedido
        OrdenPedido orden = new OrdenPedido();
        orden.setBebida(bebidaSeleccionada);
        orden.setCantidadOrdenPedido(cantidad);
        orden.setSubtotalOrdenPedido(subtotal.doubleValue());
        
        ordenesPedido.add(orden);
        actualizarTablaYTotal();
    }

    private void cancelarPedido() {
        ordenesPedido.clear();
        clienteBloqueado = null;
        totalBrutoPedido = BigDecimal.ZERO;
        
        inicializarVista();
        actualizarTablaYTotal();
    }

    private void actualizarTablaYTotal() {
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidaVenta().getModel();
        model.setRowCount(0);
        totalBrutoPedido = BigDecimal.ZERO;

        for (OrdenPedido orden : ordenesPedido) {
            Bebida bebida = orden.getBebida();
            BigDecimal subtotal = new BigDecimal(orden.getSubtotalOrdenPedido()).setScale(2, RoundingMode.HALF_UP);
            totalBrutoPedido = totalBrutoPedido.add(subtotal);

            // Determinar si hay promoción aplicada
            BigDecimal precioOriginal = bebida.getPrecio();
            BigDecimal precioAplicado = subtotal.divide(new BigDecimal(orden.getCantidadOrdenPedido()), 2, RoundingMode.HALF_UP);
            String textoPromocion = "N/A";
            if (precioAplicado.compareTo(precioOriginal) < 0) {
                 BigDecimal porc = BigDecimal.ONE.subtract(precioAplicado.divide(precioOriginal, 4, RoundingMode.HALF_UP)).multiply(new BigDecimal(100));
                 textoPromocion = String.format("%.0f%%", porc);
            }

            Object[] row = {
                bebida.getNombre(),
                orden.getCantidadOrdenPedido(),
                bebida.getPrecio(),
                subtotal,
                textoPromocion
            };
            model.addRow(row);
        }
        actualizarTotalConDescuentoGeneral();
    }
     
    private void actualizarTotalConDescuentoGeneral() {
        int porcentajeDescuento = (int) vista.getSpnDescuento().getValue();
        BigDecimal montoDescuento = totalBrutoPedido.multiply(new BigDecimal(porcentajeDescuento))
                                                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalFinal = totalBrutoPedido.subtract(montoDescuento)
                                                .setScale(2, RoundingMode.HALF_UP);

        vista.getLbTotalPedido().setText(totalFinal.toString());
    }
    
    private void guardarPedido() {
        if (ordenesPedido.isEmpty() || clienteBloqueado == null) {
            mostrarError("No hay productos en el pedido o no se ha seleccionado un cliente.");
            return;
        }

        try {
            // Calcular de nuevo el descuento y total para consistencia
            int porcentajeDescuento = (int) vista.getSpnDescuento().getValue();
            BigDecimal montoDescuento = totalBrutoPedido.multiply(new BigDecimal(porcentajeDescuento))
                                                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalFinal = totalBrutoPedido.subtract(montoDescuento)
                                                    .setScale(2, RoundingMode.HALF_UP);
            
            // Crear el objeto PedidoCliente
            PedidoCliente nuevoPedido = new PedidoCliente();
            nuevoPedido.setIdPedidoCliente(PedidoClienteDAO.obtenerUltimoIdPedido() + 1); // Generamos el nuevo ID
            nuevoPedido.setFecha(this.fechaPedidoActual);
            nuevoPedido.setDescuentoPedidoCliente(montoDescuento);
            nuevoPedido.setTotalPedidoCliente(totalFinal);
            nuevoPedido.setCliente(clienteBloqueado);

            if (PedidoClienteDAO.registrarPedidoCompleto(nuevoPedido, ordenesPedido)) {
                JOptionPane.showMessageDialog(vista, "Pedido registrado exitosamente con ID: " + nuevoPedido.getIdPedidoCliente(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cancelarPedido();
            } else {
                 mostrarError("Ocurrió un error en la base de datos al registrar el pedido.");
            }

        } catch (SQLException ex) {
            mostrarError("Error de base de datos al registrar el pedido: " + ex.getMessage());
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
         if (e.getSource() == vista.getBtnAgregarProducto()) {
            agregarBebidaATabla();
        } else if (e.getSource() == vista.getBtnGuardarPedido()) {
            guardarPedido();
        } else if (e.getSource() == vista.getBtnCancelar()) {
            cancelarPedido();
        }    
    }
}

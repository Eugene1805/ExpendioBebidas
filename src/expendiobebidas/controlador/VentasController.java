package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.dao.DetalleVentaDAO;
import expendiobebidas.modelo.dao.VentaDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.modelo.pojo.DetalleVenta;
import expendiobebidas.modelo.pojo.Venta;
import expendiobebidas.vista.Ventas;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private final VentaDAO modeloDAO;
    private BebidaDAO bebidaDAO;
    private List<Bebida> bebidas;
    private ClienteDAO clienteDAO;
    private List<Cliente> clientes;
    private DetalleVentaDAO detalleVentaDAO;
    private List<DetalleVenta> detalles;
    private Cliente clienteBloqueado = null;
    private BigDecimal totalVenta = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
    public VentasController(Ventas vista) {
        this.vista = vista;
        this.modeloDAO = new VentaDAO();
        this.bebidaDAO = new BebidaDAO();
        this.bebidas = new ArrayList<>();
        this.clienteDAO = new ClienteDAO();
        this.clientes = new ArrayList<>();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.detalles = new ArrayList<>();
        
        configurarListeners();
        cargarBebidas();
        cargarClientes();
        inicializarInformacionDefault();
    }
    
    private void configurarListeners(){
        vista.getBtnAgregarProducto().addActionListener(e->agregarBebida());
        vista.getBtnGenerarVenta().addActionListener(e->finalizarVenta());
        vista.getBtnCancelar().addActionListener(e->cancelar());
    }
    
    private void inicializarInformacionDefault(){
        
        vista.getSpCantidad().setValue(1);
        vista.getTfFechaVenta().setText(fecha);
        vista.getTfDescuentoVenta().setText("0");
        vista.getLbTotalVenta().setText("0.00");
        vista.getTfFolioVenta().setText(generarFolio());
    }
    
    private void cargarBebidas(){
        try {
            bebidas = bebidaDAO.readAll();
            cargarComboBebidas();
        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cargarClientes(){
        try {
            clientes = clienteDAO.readAll();
            cargarComboClientes();
        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cargarComboBebidas() {
        DefaultComboBoxModel<Bebida> model = new DefaultComboBoxModel<>();
        for (Bebida bebida : bebidas) {
            model.addElement(bebida);
        }
        vista.getCbBebidaVenta().setModel(model);
    }

    private void cargarComboClientes() {
        DefaultComboBoxModel<Cliente> model = new DefaultComboBoxModel<>();
        clientes.forEach(model::addElement);
        vista.getCbClienteVenta().setModel(model);
    }
    
    private void agregarBebida(){
        clienteBloqueado = (Cliente) vista.getCbClienteVenta().getSelectedItem();
        if(clienteBloqueado == null){
            mostrarError("Datos Incompletos");
            vista.getCbClienteVenta().setEnabled(false);
        }
        Bebida bebidaSeleccionada = (Bebida) vista.getCbBebidaVenta().getSelectedItem();
        if(bebidaSeleccionada == null){
            mostrarError("Bebida no seleccionada");
        }else{
            BigDecimal precio = bebidaSeleccionada.getPrecio();
            int cantidad = (int) vista.getSpCantidad().getValue();
            // Calcular subtotal
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));

            // Crear detalle temporal
            DetalleVenta detalle = new DetalleVenta();
            detalle.setBebida(bebidaSeleccionada);
            detalle.setCantidad(cantidad);
            detalle.setPrecioMomento(bebidaSeleccionada.getPrecio());
            detalle.setSubtotal(subtotal.doubleValue());
            // Agregar a lista temporal y bloquear cliente
            detalles.add(detalle);
            // Actualizar total
            totalVenta = totalVenta.add(subtotal);
            actualizarTabla();
        }        
    }
    
    private void finalizarVenta(){
        if(detalles.isEmpty() || clienteBloqueado == null)
            mostrarError("Datos Incompletos");
        
        // Actualizar total en la vista
        if(Double.parseDouble(vista.
                    getTfDescuentoVenta().getText()) > 0)
                totalVenta = totalVenta.multiply(BigDecimal.valueOf(Double.parseDouble(vista.
                    getTfDescuentoVenta().getText())));
        Venta venta = new Venta();
        venta.setFecha(fecha);
        try {
            descuento = new BigDecimal(vista.getTfDescuentoVenta().getText());
        } catch (NumberFormatException e) {
            descuento = BigDecimal.ZERO;
        } // BigDecimal (opcional)
        venta.setFolio(generarFolio());
        venta.setTotal(totalVenta.subtract(descuento != null ? descuento : BigDecimal.ZERO));
        venta.setCliente(clienteBloqueado);

        // Insertar venta
        modeloDAO.insertar(venta);

        // Insertar detalles
        for (DetalleVenta detalle : detalles) {
            DetalleVenta dv = new DetalleVenta();
            dv.setVenta(venta);
            dv.setBebida(detalle.getBebida());
            dv.setCantidad(detalle.getCantidad());
            dv.setPrecioMomento(detalle.getPrecioMomento());
            dv.setSubtotal(detalle.getSubtotal());
            detalleVentaDAO.insertar(dv);
        }
        
        JOptionPane.showMessageDialog(vista, "Venta registrada exitosamente");
        cancelar();
        vista.getCbClienteVenta().setEnabled(true);
    }
    
    private void cancelar(){
        detalles.clear();
        clienteBloqueado = null;
        totalVenta = BigDecimal.ZERO;
        descuento = BigDecimal.ZERO;
        vista.getSpCantidad().setValue(0);
        
        inicializarInformacionDefault();
        actualizarTabla(); 
    }
    
    private String generarFolio(){
        int añoActual = Year.now().getValue() % 100; 
        int siguienteNumero = modeloDAO.obtenerUltimoFolio(añoActual) + 1;
        return String.format("VENT-%02d-%03d", añoActual, siguienteNumero);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void actualizarTabla(){
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidaVenta().getModel();
        model.setRowCount(0);

        for (DetalleVenta detalle : detalles) {
            Bebida bebida = detalle.getBebida();
            Object[] row = {
                bebida.getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioMomento(),
                detalle.getSubtotal()
            };
            model.addRow(row);
        }
        vista.getLbTotalVenta().setText(totalVenta.toString());
    }
}

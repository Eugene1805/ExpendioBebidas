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
    }
    
    private void configurarListeners(){
        vista.getBtnAgregarProducto().addActionListener(e->agregarBebida());
        vista.getBtnGenerarVenta().addActionListener(e->finalizarVenta());
        vista.getBtnCancelar().addActionListener(e->cancelar());
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
        if(detalles.isEmpty() || clienteBloqueado == null)
            mostrarError("Datos Incompletos");
        
        Bebida bebidaSeleccionada = (Bebida) vista.getCbBebidaVenta().getSelectedItem();
        BigDecimal precio = BigDecimal.ZERO;
        try {
            precio = bebidaDAO.read(bebidaSeleccionada.getIdBebida()).getPrecio();
        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
        int cantidad = 0;
        // Calcular subtotal
        BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));

        // Crear detalle temporal
        DetalleVenta detalle = new DetalleVenta();
        detalle.setBebida(bebidaSeleccionada);
        detalle.setCantidad(cantidad);
        detalle.setPrecioMomento(Float.MIN_NORMAL);
        detalle.setSubtotal(Double.NaN);
        // Agregar a lista temporal y bloquear cliente
        detalles.add(detalle);
        Cliente clienteSeleccionado = (Cliente) vista.getCbClienteVenta().getSelectedItem();
        clienteBloqueado = clienteSeleccionado;

        // Actualizar total
        totalVenta = totalVenta.add(subtotal);
    }
    
    private void finalizarVenta(){
        if(detalles.isEmpty() || clienteBloqueado == null)
            mostrarError("Datos Incompletos");
        
        Venta venta = new Venta();
        venta.setFecha(fecha);
        venta.setDescuento(descuento); // BigDecimal (opcional)
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
    }
    
    private void cancelar(){
        detalles.clear();
        clienteBloqueado = null;
        totalVenta = BigDecimal.ZERO;
        descuento = BigDecimal.ZERO;
    }
    
    private String generarFolio(){
        int añoActual = Year.now().getValue() % 100; 
        int siguienteNumero = modeloDAO.obtenerUltimoFolio(añoActual) + 1;
        return String.format("VENT-%02d-%03d", añoActual, siguienteNumero);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

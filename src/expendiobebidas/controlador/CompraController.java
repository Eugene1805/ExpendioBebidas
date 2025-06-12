package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.CompraDAO;
import expendiobebidas.modelo.dao.ProveedorDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Compra;
import expendiobebidas.modelo.pojo.DetalleCompra;
import expendiobebidas.modelo.pojo.Proveedor;
import expendiobebidas.vista.Compras;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
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
 * @author eugen
 */
public class CompraController {
    private final Compras vista;
    private final CompraDAO compraDAO;
    private final BebidaDAO bebidaDAO;
    private final ProveedorDAO proveedorDAO;
    
    private List<Bebida> bebidas;
    private List<Proveedor> provedores;
    private List<DetalleCompra> detallesCompra;
    
    private Proveedor proveedorBloqueado = null;
    private BigDecimal totalCompra = BigDecimal.ZERO;
    private LocalDate fechaCompraActual;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public CompraController(Compras vista) {
        this.vista = vista;
        this.compraDAO = new CompraDAO();
        this.bebidaDAO = new BebidaDAO();
        this.proveedorDAO = new ProveedorDAO();
        
        this.bebidas = new ArrayList<>();
        this.provedores = new ArrayList<>();
        this.detallesCompra = new ArrayList<>();
        
        configurarListeners();
        cargarDatosIniciales();
        inicializarVista();
    }
    
    private void configurarListeners(){
        vista.getBtnAgregar().addActionListener(e -> agregarBebidaATabla());
        vista.getBtnCancelar().addActionListener(e -> cancelarCompra());
        vista.getBtnRegistrarCompra().addActionListener(e -> finalizarCompra());
    }
    
    private void cargarDatosIniciales(){
        try {
            bebidas = BebidaDAO.readAll();
            DefaultComboBoxModel<Bebida> bebidaModel = new DefaultComboBoxModel<>();
            bebidas.forEach(bebidaModel::addElement);
            vista.getCbBebidaCompra().setModel(bebidaModel);

            provedores = ProveedorDAO.readAll();
            DefaultComboBoxModel<Proveedor> proveedorModel = new DefaultComboBoxModel<>();
            provedores.forEach(proveedorModel::addElement);
            vista.getCbProveedorCompra().setModel(proveedorModel);
        } catch (SQLException ex) {
            mostrarError("Error al cargar datos iniciales: " + ex.getMessage());
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void inicializarVista(){
        fechaCompraActual = LocalDate.now();
        vista.getTfFecha().setText(fechaCompraActual.format(DATE_FORMATTER));

        vista.getSpnCantidad().setValue(1);
        vista.getLbTotal().setText("0.00");
        vista.getTfFolio().setText(generarFolio());
        vista.getCbProveedorCompra().setEnabled(true);
        
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidas().getModel();
        model.setRowCount(0);
        totalCompra = BigDecimal.ZERO;
    }
    
    private String generarFolio() {
        try {
            int anioActual = Year.now().getValue() % 100;
            int siguienteNumero = compraDAO.obtenerUltimoNumeroFolio(anioActual) + 1;
            return String.format("COMP-%02d-%03d", anioActual, siguienteNumero);
        } catch (SQLException ex) {
            mostrarError("Error al generar el folio: " + ex.getMessage());
            return "COMP-ERR-000";
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void agregarBebidaATabla(){
        Bebida bebidaSeleccionada = (Bebida) vista.getCbBebidaCompra().getSelectedItem();
        if (bebidaSeleccionada == null) {
            mostrarError("Debe seleccionar una bebida.");
            return;
        }

        int cantidad = (int) vista.getSpnCantidad().getValue();
        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor a cero.");
            return;
        }
        
        // Bloquear proveedor si es el primer producto
        if (proveedorBloqueado == null) {
            proveedorBloqueado = (Proveedor) vista.getCbProveedorCompra().getSelectedItem();
            if (proveedorBloqueado == null) {
                mostrarError("Debe seleccionar un proveedor.");
                return;
            }
            vista.getCbProveedorCompra().setEnabled(false);
        }

        // En una compra, el "precio" es el costo de adquisición. Usaremos el precio de venta como referencia.
        BigDecimal costoUnitario = bebidaSeleccionada.getPrecio();
        BigDecimal subtotal = costoUnitario.multiply(new BigDecimal(cantidad));

        // Crear el objeto de la cabecera de compra para asociarlo al detalle
        Compra compraCabecera = new Compra();
        compraCabecera.setFolio(vista.getTfFolio().getText());

        // Crear el detalle
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCompra(compraCabecera); // Asociar la cabecera (solo con el folio por ahora)
        detalle.setBebida(bebidaSeleccionada);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(subtotal.doubleValue());
        
        detallesCompra.add(detalle);
        actualizarTablaYTotal();
    }
    
    private void finalizarCompra(){
        if (detallesCompra.isEmpty() || proveedorBloqueado == null) {
            mostrarError("No hay productos en la compra o no se ha seleccionado un proveedor.");
            return;
        }

        try {
            // Crear el objeto Compra final con todos los datos
            Compra compraFinal = new Compra();
            compraFinal.setFolio(vista.getTfFolio().getText());
            compraFinal.setFechaCompra(this.fechaCompraActual);
            compraFinal.setTotalCompra(this.totalCompra);

            // Llamar al método transaccional del DAO
            if (compraDAO.registrarCompraCompleta(compraFinal, detallesCompra)) {
                JOptionPane.showMessageDialog(vista, 
                    "Compra registrada exitosamente con folio: " + compraFinal.getFolio(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cancelarCompra(); // Reiniciar para una nueva compra
                
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
                        if(comp instanceof expendiobebidas.vista.Proveedores proveedoresPanel){
                            new ProveedorController(proveedoresPanel);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            mostrarError("Error en la base de datos al registrar la compra: " + ex.getMessage());
            Logger.getLogger(CompraController.class.getName()).log(Level.SEVERE, "Error en transacción de compra", ex);
        }
        
    }
    
    private void actualizarTablaYTotal(){
        DefaultTableModel model = (DefaultTableModel) vista.getTbBebidas().getModel();
        model.setRowCount(0);

        totalCompra = BigDecimal.ZERO; // Reiniciar para recalcular

        for (DetalleCompra detalle : detallesCompra) {
            Bebida bebida = detalle.getBebida();
            BigDecimal subtotal = new BigDecimal(detalle.getSubtotal()).setScale(2, RoundingMode.HALF_UP);
            totalCompra = totalCompra.add(subtotal);

            Object[] row = {
                bebida.getNombre(),
                detalle.getCantidad(),
                bebida.getPrecio(), // Muestra el precio de referencia
                subtotal
            };
            model.addRow(row);
        }

        vista.getLbTotal().setText(totalCompra.setScale(2, RoundingMode.HALF_UP).toString());
    }
    
    private void cancelarCompra(){
        detallesCompra.clear();
        proveedorBloqueado = null;
        totalCompra = BigDecimal.ZERO;
        
        inicializarVista();
        actualizarTablaYTotal();
    }
}


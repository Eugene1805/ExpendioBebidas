/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.PedidoProveedorDAO;
import expendiobebidas.modelo.dao.ProveedorDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.DetallePedido;
import expendiobebidas.modelo.pojo.Pedido;
import expendiobebidas.modelo.pojo.Proveedor;
import expendiobebidas.vista.PedidoProveedor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

/**
 *
 * @author Nash
 */
public class PedidoProveedorController implements ActionListener, TableModelListener{
    
    private final PedidoProveedor vista;
    private DefaultTableModel tableModel;
    
    public PedidoProveedorController(PedidoProveedor vista) {
        // QUITAR 
        System.out.println("--- INICIANDO PedidoProveedorController ---"); // Para saber que se crea
        this.vista = vista;
        this.inicializarComponentes();
        this.registrarEventos();
        //Quitar
        System.out.println("--- PedidoProveedorController INICIADO ---");
    }
    
    private void inicializarComponentes(){
        configurarTabla();
        cargarProveedores();
        cargarBebidas();
        cargarBebidasConStockBajo();
        
        vista.getTfFechaPedido().setText(LocalDate.now().toString());
        vista.getTfFechaPedido().setEditable(false);
    }
    
    private void registrarEventos(){
        vista.getBtnAddBebida().addActionListener(this);
        vista.getBtnGuardarPedido().addActionListener(this);
        vista.getBtnAceptarAddBebida().addActionListener(this);
        vista.getBtnCancelarAddBebida().addActionListener(this);
    }
    
    private void configurarTabla(){
        String[] columnas = {"ID Bebida", "Nombre", "Precio", "Stock Actual", "Cantidad a Pedir", "Subtotal"};
        tableModel = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return  false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 2: return BigDecimal.class;
                    case 3: case 4: return Integer.class;
                    case 5: return Double.class;
                    default: return String.class;
                }
            }
        };
        vista.getTblBebidas().setModel(tableModel);
        
        vista.getTblBebidas().getColumnModel().getColumn(0).setMinWidth(0);
        vista.getTblBebidas().getColumnModel().getColumn(0).setMaxWidth(0);
        vista.getTblBebidas().getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void cargarProveedores(){
        try {
            //QUITAR
            System.out.println("Cargando proveedores...");

            List<Proveedor> proveedores = ProveedorDAO.readAll();
            //QUITAR
                    System.out.println("Proveedores encontrados: " + proveedores.size()); // ¿Cuántos encontró?

            DefaultComboBoxModel<Proveedor> model = new DefaultComboBoxModel<>();
            for (Proveedor p : proveedores){
                model.addElement(p);
                
            }
            vista.getCbProveedores().setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, 
                    "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarBebidas(){
        try {
            
            List<Bebida> bebidas = BebidaDAO.readAll();
            DefaultComboBoxModel<Bebida> model = new  DefaultComboBoxModel<>();
            for (Bebida b : bebidas) {
                model.addElement(b);
            }
            vista.getCbBebidas().setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar bebidas: " 
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarBebidasConStockBajo(){
        try {
            //QUITAR
                    System.out.println("Cargando bebidas con stock bajo...");

            List<Bebida> bebidasConStockBajo = BebidaDAO.readAllStockBajo();
//QUITAR
  System.out.println("Bebidas con stock bajo encontradas: " + bebidasConStockBajo.size()); // ¿Cuántas encontró?
        
        if (bebidasConStockBajo.isEmpty()) {
            System.out.println("No hay bebidas para cargar en la tabla inicialmente.");
        }
        
            for (Bebida b : bebidasConStockBajo) {
                int cantidadAPedir = b.getStockMinimo() - b.getStockActual();
                if (cantidadAPedir < 0) cantidadAPedir = 0;
                
                double subtotal = b.getPrecio().doubleValue() * cantidadAPedir;
                
                Object[] rowData = {b.getIdBebida(), b.getNombre(), b.getPrecio(), b.getStockActual(), cantidadAPedir, subtotal};
                tableModel.addRow(rowData);
            }
            actualizarTotal();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar bebidas con stock bajo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    private void actualizarTotal(){
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            // Solo sumamos el subtotal si la cantidad a pedir es mayor que cero
            int cantidad = (Integer) tableModel.getValueAt(i, 4);
            if (cantidad > 0) {
                 total += (double) tableModel.getValueAt(i, 5);
            }
        }
        vista.getLblTotal().setText(String.format("%.2f", total));
    }
    
    private void guardarPedido(){
         if (vista.getCbProveedores().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un proveedor.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- VALIDACIÓN CORREGIDA ---
        // 1. Recolectamos solo los detalles válidos (cantidad > 0)
        List<DetallePedido> detallesParaGuardar = new ArrayList<>();
        double totalCalculado = 0.0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int cantidad = (Integer) tableModel.getValueAt(i, 4); // Columna "Cantidad a Pedir"
            
            if (cantidad > 0) {
                DetallePedido detalle = new DetallePedido();
                Bebida bebida = new Bebida();
                bebida.setIdBebida((Integer) tableModel.getValueAt(i, 0)); // ID oculto
                
                detalle.setBebida(bebida);
                detalle.setCantidad(cantidad);
                detalle.setSubtotal((Double) tableModel.getValueAt(i, 5));
                detallesParaGuardar.add(detalle);
                totalCalculado += detalle.getSubtotal();
            }
        }
        
        // 2. Verificamos si hay algo que guardar
        if (detallesParaGuardar.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay bebidas con cantidad a pedir mayor que cero. Nada que guardar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Creamos el objeto Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setProveedor((Proveedor) vista.getCbProveedores().getSelectedItem());
        nuevoPedido.setFechaPedido(LocalDate.parse(vista.getTfFechaPedido().getText()));
        nuevoPedido.setTotal(totalCalculado); // Usamos el total calculado de los items válidos
        nuevoPedido.setEstadoPedido("pendiente");

        // 4. Llamamos al DAO correcto
        boolean exito = PedidoProveedorDAO.registrarPedidoCompleto(nuevoPedido, detallesParaGuardar);
        
        if (exito) {
            JOptionPane.showMessageDialog(vista, "Pedido guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarVista();
        } else {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error al guardar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void limpiarVista() {
        tableModel.setRowCount(0);
        if (vista.getCbProveedores().getItemCount() > 0) {
            vista.getCbProveedores().setSelectedIndex(0);
        }
        vista.getLblTotal().setText("0.00");
        // Opcional: Recargar la lista de stock bajo por si se quiere hacer otro pedido inmediatamente
        cargarBebidasConStockBajo();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardarPedido()) {
            guardarPedido();
        } else if (e.getSource() == vista.getBtnAddBebida()) {
            // Lógica para añadir bebida manualmente
            vista.getSpCantidadBebidas().setValue(1); // Reiniciar spinner
            vista.getDialogAddBebida().pack();
            vista.getDialogAddBebida().setLocationRelativeTo(vista);
            vista.getDialogAddBebida().setVisible(true);
        } else if (e.getSource() == vista.getBtnAceptarAddBebida()) {
            agregarBebidaManualmente();
        } else if (e.getSource() == vista.getBtnCancelarAddBebida()) {
            vista.getDialogAddBebida().setVisible(false);
        }
    }

    private void agregarBebidaManualmente() {
        Bebida bebidaSeleccionada = (Bebida) vista.getCbBebidas().getSelectedItem();
        int cantidad = (Integer) vista.getSpCantidadBebidas().getValue();
        
        if (bebidaSeleccionada == null || cantidad <= 0) {
            JOptionPane.showMessageDialog(vista.getDialogAddBebida(), "Seleccione una bebida y una cantidad mayor a cero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double subtotal = bebidaSeleccionada.getPrecio().doubleValue() * cantidad;
        Object[] rowData = {
            bebidaSeleccionada.getIdBebida(),
            bebidaSeleccionada.getNombre(),
            bebidaSeleccionada.getPrecio(),
            bebidaSeleccionada.getStockActual(),
            cantidad,
            subtotal
        };
        tableModel.addRow(rowData);
        actualizarTotal(); // Actualizamos el total general
        vista.getDialogAddBebida().setVisible(false);
    }

    @Override
    public void tableChanged(TableModelEvent e) {

    }    
}

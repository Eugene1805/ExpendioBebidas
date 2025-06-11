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



public class PedidoProveedorController {
        //implements TableModelListener {
 /*  
    private final PedidoProveedor vista;
    private DefaultTableModel tableModel;

    public PedidoProveedorController(PedidoProveedor vista) {
        this.vista = vista;
        this.inicializarComponentes();
        this.registrarEventos();
    }
    
    private void inicializarComponentes() {
        configurarTabla();
        cargarProveedores();
        cargarBebidasParaDialogo();
        cargarBebidasConStockBajo(); 
        
        vista.getTfFechaPedido().setText(LocalDate.now().toString());
        vista.getTfFechaPedido().setEditable(false);
    }

    private void registrarEventos() {
        // Registramos los listeners para los botones y la tabla
        vista.getBtnAddBebida().addActionListener(this);
        vista.getBtnGuardarPedido().addActionListener(this);
        vista.getBtnAceptarAddBebida().addActionListener(this);
        vista.getBtnCancelarAddBebida().addActionListener(this);
        tableModel.addTableModelListener(this);
    }
    
    private void configurarTabla() {
        String[] columnas = {"ID Bebida", "Nombre", "Precio", "Stock Actual", "Cantidad a Pedir", "Subtotal"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna "Cantidad a Pedir" es editable
                return column == 4;
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
        // Ocultamos la columna del ID para que el usuario no la vea, pero podamos acceder a ella
        vista.getTblBebidas().getColumnModel().getColumn(0).setMinWidth(0);
        vista.getTblBebidas().getColumnModel().getColumn(0).setMaxWidth(0);
        vista.getTblBebidas().getColumnModel().getColumn(0).setWidth(0);
    }

    private void cargarProveedores() {
        try {
            // Asumo que tienes un ProveedorDAO con un método estático readAll
            List<Proveedor> proveedores = ProveedorDAO.readAll();
            DefaultComboBoxModel<Proveedor> model = new DefaultComboBoxModel<>();
            for (Proveedor p : proveedores) {
                model.addElement(p);
            }
            vista.getCbProveedores().setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarBebidasParaDialogo() {
        try {
            List<Bebida> bebidas = BebidaDAO.readAll();
            DefaultComboBoxModel<Bebida> model = new DefaultComboBoxModel<>();
            for (Bebida b : bebidas) {
                model.addElement(b);
            }
            vista.getCbBebidas().setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar bebidas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ESTE ES EL MÉTODO CLAVE MODIFICADO ---
    private void cargarBebidasConStockBajo() {
        try {
            List<Bebida> bebidasConStockBajo = BebidaDAO.readAllStockBajo();
            for (Bebida b : bebidasConStockBajo) {
                // Calculamos la cantidad necesaria para alcanzar el stock mínimo
                int cantidadAPedir = b.getStockMinimo() - b.getStockActual();
                // Aseguramos que la cantidad no sea negativa (por si acaso)
                if (cantidadAPedir < 0) cantidadAPedir = 0;
                
                double subtotal = b.getPrecio().doubleValue() * cantidadAPedir;
                
                Object[] rowData = {
                    b.getIdBebida(),
                    b.getNombre(),
                    b.getPrecio(),
                    b.getStockActual(),
                    cantidadAPedir,
                    subtotal
                };
                tableModel.addRow(rowData);
            }
            // Después de cargar, actualizamos el total
            actualizarTotal();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar bebidas con stock bajo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTotal() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 5); // Columna Subtotal (índice 5)
        }
        vista.getLblTotal().setText(String.format("%.2f", total));
    }
    
    private void guardarPedido() {
        if (vista.getCbProveedores().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un proveedor.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "Debe agregar al menos una bebida al pedido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setProveedor((Proveedor) vista.getCbProveedores().getSelectedItem());
        nuevoPedido.setFechaPedido(LocalDate.parse(vista.getTfFechaPedido().getText()));
        nuevoPedido.setTotal(Double.parseDouble(vista.getLblTotal().getText().replace(",", ".")));
        nuevoPedido.setEstadoPedido("pendiente"); // Estado inicial del pedido
        
        List<DetallePedido> detalles = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            DetallePedido detalle = new DetallePedido();
            Bebida bebida = new Bebida();
            bebida.setIdBebida((Integer) tableModel.getValueAt(i, 0)); // ID oculto
            
            detalle.setBebida(bebida);
            detalle.setCantidad((Integer) tableModel.getValueAt(i, 4));
            detalle.setSubtotal((Double) tableModel.getValueAt(i, 5));
            detalles.add(detalle);
        }
        
        // Usamos el DAO de Pedido para guardar (asumiendo que tienes uno llamado PedidoDAO)
        boolean exito = PedidoDAO.guardarPedido(nuevoPedido, detalles);
        
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
        if (e.getSource() == vista.getBtnAddBebida()) {
            vista.getDialogAddBebida().pack();
            vista.getDialogAddBebida().setLocationRelativeTo(vista);
            vista.getDialogAddBebida().setVisible(true);
        } else if (e.getSource() == vista.getBtnGuardarPedido()) {
            guardarPedido();
        } else if (e.getSource() == vista.getBtnAceptarAddBebida()) {
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
            vista.getDialogAddBebida().setVisible(false);
        } else if (e.getSource() == vista.getBtnCancelarAddBebida()) {
            vista.getDialogAddBebida().setVisible(false);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            
            if (column == 4) { // Columna "Cantidad a Pedir"
                BigDecimal precio = (BigDecimal) tableModel.getValueAt(row, 2);
                int cantidad = (Integer) tableModel.getValueAt(row, 4);
                double nuevoSubtotal = precio.doubleValue() * cantidad;
                
                tableModel.removeTableModelListener(this);
                tableModel.setValueAt(nuevoSubtotal, row, 5); // Actualiza subtotal
                tableModel.addTableModelListener(this);
            }
        }
        actualizarTotal();
    } 
*/
}

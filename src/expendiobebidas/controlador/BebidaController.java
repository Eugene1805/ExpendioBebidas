package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.vista.Bebidas;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author eugen
 */
public class BebidaController {
    private final Bebidas vista;
    private final BebidaDAO modeloDAO;
    private List<Bebida> listaBebidas;
    
    public BebidaController(Bebidas vista) {
        this.vista = vista;
        this.modeloDAO = new BebidaDAO();
        this.listaBebidas = new ArrayList<>();
        
        configurarListeners();
        cargarBebidas();
    }
    
    private void configurarListeners() {
        vista.getBtnAddBebida().addActionListener(e -> mostrarDialogoRegistro());
        vista.getBtnGuardarRegistroBebida().addActionListener(e -> guardarBebida());
        vista.getBtnCancelarRegistroBebida().addActionListener(e -> cerrarDialogoRegistro());
        vista.getBtnActualizarBebida().addActionListener(e -> actualizarBebida());
        vista.getBtnEliminarBebida().addActionListener(e -> eliminarBebida());
        
        // Listener para selección en la tabla
        vista.getTblBebidas().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                habilitarBotonesEdicion();
            }
        });
    }
    
    private void restaurarListenerOriginal(){
        vista.getBtnGuardarRegistroBebida().removeActionListener(vista.getBtnGuardarRegistroBebida().
                getActionListeners()[0]);
        vista.getBtnGuardarRegistroBebida().addActionListener(ev -> guardarBebida());
        vista.getBtnGuardarRegistroBebida().setText("Guardar");
        vista.getDialogRegistrarBebida().setTitle("Registrar Bebida");
    }
    
    private void cargarBebidas() {
        try {
            listaBebidas = BebidaDAO.readAll();
            actualizarTabla();
        } catch (SQLException ex) {
            mostrarError("Error al cargar bebidas: " + ex.getMessage());
        }
    }
    
    private void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTblBebidas().getModel();
        model.setRowCount(0); // Limpiar la tabla
        
        for (Bebida bebida : listaBebidas) {
            Object[] row = {
                bebida.getNombre(),
                bebida.getPrecio(),
                bebida.getStockActual(),
                bebida.getDescripcion()
            };
            model.addRow(row);
        }
    }
    
    private void mostrarDialogoRegistro() {
        limpiarCamposDialogo();
        vista.getDialogRegistrarBebida().pack();
        vista.getDialogRegistrarBebida().setLocationRelativeTo(vista);
        vista.getDialogRegistrarBebida().setVisible(true);
    }
    
    private void cerrarDialogoRegistro() {
        vista.getDialogRegistrarBebida().setVisible(false);
    }
    
    private void limpiarCamposDialogo() {
        vista.getTfNombreBebida().setText("");
        vista.getSpPrecioBebida().setValue(0.0f);
        vista.getSpStockMinimo().setValue(0);
        vista.getSpStockActual().setValue(0);
        vista.getTaDescripcionBebida().setText("");
    }
    
    private void guardarBebida() {
        try {
            Bebida nuevaBebida = new Bebida();
            nuevaBebida.setNombre(vista.getTfNombreBebida().getText());
            nuevaBebida.setPrecio((Float)vista.getSpPrecioBebida().getValue());
            nuevaBebida.setStockMinimo((Integer)vista.getSpStockMinimo().getValue());
            nuevaBebida.setStockActual((Integer)vista.getSpStockActual().getValue());
            nuevaBebida.setDescripcion(vista.getTaDescripcionBebida().getText());
            
            if (modeloDAO.create(nuevaBebida)) {
                JOptionPane.showMessageDialog(vista, "Bebida registrada con éxito");
                cerrarDialogoRegistro();
                cargarBebidas();
            } else {
                mostrarError("No se pudo registrar la bebida");
            }
        } catch (SQLException ex) {
            mostrarError("Error al guardar bebida: " + ex.getMessage());
        } catch (HeadlessException ex) {
            mostrarError("Datos inválidos: " + ex.getMessage());
        }
    }
    
    private void habilitarBotonesEdicion() {
        int selectedRow = vista.getTblBebidas().getSelectedRow();
        boolean habilitar = selectedRow != -1;
        
        vista.getBtnActualizarBebida().setEnabled(habilitar);
        vista.getBtnEliminarBebida().setEnabled(habilitar);
    }
    
    private void actualizarBebida() {
        int selectedRow = vista.getTblBebidas().getSelectedRow();
        if (selectedRow == -1) return;
        
        Bebida bebidaSeleccionada = listaBebidas.get(selectedRow);
        
        // Llenar el diálogo con los datos de la bebida seleccionada
        vista.getTfNombreBebida().setText(bebidaSeleccionada.getNombre());
        vista.getSpPrecioBebida().setValue(bebidaSeleccionada.getPrecio());
        vista.getSpStockMinimo().setValue(bebidaSeleccionada.getStockMinimo());
        vista.getSpStockActual().setValue(bebidaSeleccionada.getStockActual());
        vista.getTaDescripcionBebida().setText(bebidaSeleccionada.getDescripcion());
        
        // Cambiar el texto del botón a "Actualizar"
        vista.getBtnGuardarRegistroBebida().setText("Actualizar");
        
        // Mostrar el diálogo
        vista.getDialogRegistrarBebida().setTitle("Actualizar Bebida");
        vista.getDialogRegistrarBebida().pack();
        vista.getDialogRegistrarBebida().setLocationRelativeTo(vista);
        vista.getDialogRegistrarBebida().setVisible(true);
        
        // Cambiar el listener temporalmente para actualizar
        vista.getBtnGuardarRegistroBebida().removeActionListener(vista.getBtnGuardarRegistroBebida().
                getActionListeners()[0]);
        vista.getBtnGuardarRegistroBebida().addActionListener(e -> {
            try {
                bebidaSeleccionada.setNombre(vista.getTfNombreBebida().getText());
                bebidaSeleccionada.setPrecio((Float)vista.getSpPrecioBebida().getValue());
                bebidaSeleccionada.setStockMinimo((Integer)vista.getSpStockMinimo().getValue());
                bebidaSeleccionada.setStockActual((Integer)vista.getSpStockActual().getValue());
                bebidaSeleccionada.setDescripcion(vista.getTaDescripcionBebida().getText());
                
                if (BebidaDAO.update(bebidaSeleccionada)) {
                    JOptionPane.showMessageDialog(vista, "Bebida actualizada con éxito");
                    cerrarDialogoRegistro();
                    cargarBebidas();
                    restaurarListenerOriginal();
                } else {
                    mostrarError("No se pudo actualizar la bebida");
                }
            } catch (SQLException ex) {
                mostrarError("Error al actualizar bebida: " + ex.getMessage());
            } catch (HeadlessException ex) {
                mostrarError("Datos inválidos: " + ex.getMessage());
            }
        });
    }
    
    private void eliminarBebida() {
        int selectedRow = vista.getTblBebidas().getSelectedRow();
        if (selectedRow == -1) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro que desea eliminar esta bebida?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                Bebida bebidaAEliminar = listaBebidas.get(selectedRow);
                if (BebidaDAO.delete(bebidaAEliminar.getIdBebida())) {
                    JOptionPane.showMessageDialog(vista, "Bebida eliminada con éxito");
                    cargarBebidas();
                } else {
                    mostrarError("No se pudo eliminar la bebida");
                }
            } catch (SQLException ex) {
                mostrarError("Error al eliminar bebida: " + ex.getMessage());
            }
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

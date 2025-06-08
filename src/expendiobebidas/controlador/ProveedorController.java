package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ProveedorDAO;
import expendiobebidas.modelo.pojo.Proveedor;
import expendiobebidas.vista.Proveedores;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
/**
 *
 * @author eugen
 */
public class ProveedorController {
    private final Proveedores vista;
    private final ProveedorDAO modeloDAO;
    private List<Proveedor> listaProveedores;
    
    public ProveedorController(Proveedores vista) {
        this.vista = vista;
        this.modeloDAO = new ProveedorDAO();
        this.listaProveedores = new ArrayList<>();
        
        configurarListeners();
        cargarProveedores();
    }
    
    private void configurarListeners() {
        vista.getBtnRegistrarProveedor().addActionListener(e -> mostrarDialogoRegistro());
        vista.getBtnGuardarProveedor().addActionListener(e -> guardarProveedor());
        vista.getBtnCancelarProveedor().addActionListener(e -> cerrarDialogoRegistro());
        vista.getBtnActualizarProveedor().addActionListener(e -> actualizarProveedor());
        vista.getBtnEliminarProveedor().addActionListener(e -> eliminarProveedor());
        
        // Listener para selección en la tabla
        vista.getTblProveedores().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                habilitarBotonesEdicion();
            }
        });
    }
    
    private void restaurarListenerOriginal(){
        vista.getBtnGuardarProveedor().removeActionListener(vista.getBtnGuardarProveedor().getActionListeners()[0]);
        vista.getBtnGuardarProveedor().addActionListener(ev -> guardarProveedor());
        vista.getBtnGuardarProveedor().setText("Guardar");
        vista.getDialogRegistrarProveedor().setTitle("Registrar Proveedor");
    }
    
    private void cargarProveedores() {
        try {
            listaProveedores = ProveedorDAO.readAll();
            actualizarTabla();
        } catch (SQLException ex) {
            mostrarError("Error al cargar proveedores: " + ex.getMessage());
        }
    }
    
    private void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTblProveedores().getModel();
        model.setRowCount(0); // Limpiar la tabla
        
        for (Proveedor proveedor : listaProveedores) {
            Object[] row = {
                proveedor.getNombre(),
                proveedor.getCorreo(),
                proveedor.getTelefono(),
                proveedor.getDireccion()
            };
            model.addRow(row);
        }
    }
    
    private void mostrarDialogoRegistro() {
        limpiarCamposDialogo();
        vista.getDialogRegistrarProveedor().pack();
        vista.getDialogRegistrarProveedor().setLocationRelativeTo(vista);
        vista.getDialogRegistrarProveedor().setVisible(true);
    }
    
    private void cerrarDialogoRegistro() {
        vista.getDialogRegistrarProveedor().setVisible(false);
    }
    
    private void limpiarCamposDialogo() {
        vista.getTfNombreProveedor().setText("");
        vista.getTfCorreoProveedor().setText("");
        vista.getTfTelefonoProveedor().setText("");
        vista.getTaDireccionProveedor().setText("");
    }
    
    private void guardarProveedor() {
        try {
            if (!validarDatosProveedor()) {
                return;
            }
            
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombre(vista.getTfNombreProveedor().getText().trim());
            nuevoProveedor.setCorreo(vista.getTfCorreoProveedor().getText().trim());
            nuevoProveedor.setTelefono(vista.getTfTelefonoProveedor().getText().trim());
            nuevoProveedor.setDireccion(vista.getTaDireccionProveedor().getText().trim());
            
            if (modeloDAO.create(nuevoProveedor)) {
                JOptionPane.showMessageDialog(vista.getDialogRegistrarProveedor(), "Proveedor registrado con éxito");
                cerrarDialogoRegistro();
                cargarProveedores();
            } else {
                mostrarError("No se pudo registrar el proveedor");
            }
        } catch (SQLException ex) {
            mostrarError("Error al guardar proveedor: " + ex.getMessage());
        }
    }
    
    private boolean validarDatosProveedor() {
        String nombre = vista.getTfNombreProveedor().getText().trim();
        String correo = vista.getTfCorreoProveedor().getText().trim();
        String telefono = vista.getTfTelefonoProveedor().getText().trim();
        
        if (nombre.isEmpty()) {
            mostrarError("El nombre del proveedor es obligatorio");
            return false;
        }
        
        if (!correo.isEmpty() && !correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarError("El formato del correo electrónico no es válido");
            return false;
        }
        
        if (!telefono.matches("^[0-9]{10}$")) {
            mostrarError("El teléfono debe tener 10 dígitos");
            return false;
        }
        
        return true;
    }
    
    private void habilitarBotonesEdicion() {
        int selectedRow = vista.getTblProveedores().getSelectedRow();
        boolean habilitar = selectedRow != -1;
        
        vista.getBtnActualizarProveedor().setEnabled(habilitar);
        vista.getBtnEliminarProveedor().setEnabled(habilitar);
    }
    
    private void actualizarProveedor() {
        int selectedRow = vista.getTblProveedores().getSelectedRow();
        if (selectedRow == -1) return;
        
        Proveedor proveedorSeleccionado = listaProveedores.get(selectedRow);
        
        // Llenar el diálogo con los datos del proveedor seleccionado
        vista.getTfNombreProveedor().setText(proveedorSeleccionado.getNombre());
        vista.getTfCorreoProveedor().setText(proveedorSeleccionado.getCorreo());
        vista.getTfTelefonoProveedor().setText(proveedorSeleccionado.getTelefono());
        vista.getTaDireccionProveedor().setText(proveedorSeleccionado.getDireccion());
        
        // Cambiar el texto del botón a "Actualizar"
        vista.getBtnGuardarProveedor().setText("Actualizar");
        
        // Mostrar el diálogo
        vista.getDialogRegistrarProveedor().setTitle("Actualizar Proveedor");
        vista.getDialogRegistrarProveedor().pack();
        vista.getDialogRegistrarProveedor().setLocationRelativeTo(vista);
        vista.getDialogRegistrarProveedor().setVisible(true);
        
        // Cambiar el listener temporalmente para actualizar
        vista.getBtnGuardarProveedor().removeActionListener(vista.getBtnGuardarProveedor().getActionListeners()[0]);
        vista.getBtnGuardarProveedor().addActionListener(e -> {
            try {
                if (!validarDatosProveedor()) {
                    return;
                }
                
                proveedorSeleccionado.setNombre(vista.getTfNombreProveedor().getText().trim());
                proveedorSeleccionado.setCorreo(vista.getTfCorreoProveedor().getText().trim());
                proveedorSeleccionado.setTelefono(vista.getTfTelefonoProveedor().getText().trim());
                proveedorSeleccionado.setDireccion(vista.getTaDireccionProveedor().getText().trim());
                
                if (ProveedorDAO.update(proveedorSeleccionado)) {
                    JOptionPane.showMessageDialog(vista, "Proveedor actualizado con éxito");
                    cerrarDialogoRegistro();
                    cargarProveedores();
                    restaurarListenerOriginal();
                } else {
                    mostrarError("No se pudo actualizar el proveedor");
                }
            } catch (SQLException ex) {
                mostrarError("Error al actualizar proveedor: " + ex.getMessage());
            }
        });
    }
    
    private void eliminarProveedor() {
        int selectedRow = vista.getTblProveedores().getSelectedRow();
        if (selectedRow == -1) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro que desea eliminar este proveedor?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                Proveedor proveedorAEliminar = listaProveedores.get(selectedRow);
                if (ProveedorDAO.delete(proveedorAEliminar.getIdProveedor())) {
                    JOptionPane.showMessageDialog(vista, "Proveedor eliminado con éxito");
                    cargarProveedores();
                } else {
                    mostrarError("No se pudo eliminar el proveedor");
                }
            } catch (SQLException ex) {
                mostrarError("Error al eliminar proveedor: " + ex.getMessage());
            }
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

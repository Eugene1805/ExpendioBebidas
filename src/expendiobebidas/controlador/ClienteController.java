package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.vista.Clientes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

/**
 *
 * @author eugen
 */
public class ClienteController {
    private Clientes vista;
    private ClienteDAO modeloDAO;
    private List<Cliente> listaClientes;
    
    public ClienteController(Clientes vista) {
        this.vista = vista;
        this.modeloDAO = new ClienteDAO();
        this.listaClientes = new ArrayList<>();
        
        configurarListeners();
        cargarClientes();
    }
    
    private void configurarListeners() {
        vista.getBtnAddCliente().addActionListener(e -> mostrarDialogoRegistro());
        vista.getBtnGuardarRegistroCliente().addActionListener(e -> guardarCliente());
        vista.getBtnCancelarRegistroCliente().addActionListener(e -> cerrarDialogoRegistro());
        vista.getBtnActualizarCliente().addActionListener(e -> actualizarCliente());
        vista.getBtnEliminarCliente().addActionListener(e -> eliminarCliente());
        
        // Listener para selección en la tabla
        vista.getTblClientes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                habilitarBotonesEdicion();
            }
        });
    }
    
    private void cargarClientes() {
        try {
            listaClientes = ClienteDAO.readAll();
            actualizarTabla();
        } catch (SQLException ex) {
            mostrarError("Error al cargar clientes: " + ex.getMessage());
        }
    }
    
    private void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTblClientes().getModel();
        model.setRowCount(0); // Limpiar la tabla
        
        for (Cliente cliente : listaClientes) {
            Object[] row = {
                cliente.getRazonSocialCliente(),
                cliente.getRfc(),
                cliente.getTelefonoCliente(),
                cliente.getTipo(),
                cliente.getDireccionCliente()
            };
            model.addRow(row);
        }
    }
    
    private void mostrarDialogoRegistro() {
        limpiarCamposDialogo();
        vista.getDialogRegistrarCliente().pack();
        vista.getDialogRegistrarCliente().setLocationRelativeTo(vista);
        vista.getDialogRegistrarCliente().setVisible(true);
    }
    
    private void cerrarDialogoRegistro() {
        vista.getDialogRegistrarCliente().setVisible(false);
    }
    
    private void limpiarCamposDialogo() {
        vista.getTfRazonSocialCliente().setText("");
        vista.getTfRfcCliente().setText("");
        vista.getTfTelefonoCliente().setText("");
        vista.getTfTipoCliente().setText("");
        vista.getTaDireccionCliente().setText("");
    }
    
    private void guardarCliente() {
        try {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setRazonSocialCliente(vista.getTfRazonSocialCliente().getText());
            nuevoCliente.setRfc(vista.getTfRfcCliente().getText());
            nuevoCliente.setTelefonoCliente(vista.getTfTelefonoCliente().getText());
            nuevoCliente.setTipo(vista.getTfTipoCliente().getText());
            nuevoCliente.setDireccionCliente(vista.getTaDireccionCliente().getText());
            
            if (modeloDAO.create(nuevoCliente)) {
                JOptionPane.showMessageDialog(vista, "Cliente registrado con éxito");
                cerrarDialogoRegistro();
                cargarClientes();
            } else {
                mostrarError("No se pudo registrar el cliente");
            }
        } catch (SQLException ex) {
            mostrarError("Error al guardar cliente: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Datos inválidos: " + ex.getMessage());
        }
    }
    
    private void habilitarBotonesEdicion() {
        int selectedRow = vista.getTblClientes().getSelectedRow();
        boolean habilitar = selectedRow != -1;
        
        vista.getBtnActualizarCliente().setEnabled(habilitar);
        vista.getBtnEliminarCliente().setEnabled(habilitar);
    }
    
    private void actualizarCliente() {
        int selectedRow = vista.getTblClientes().getSelectedRow();
        if (selectedRow == -1) return;
        
        Cliente clienteSeleccionado = listaClientes.get(selectedRow);
        
        // Llenar el diálogo con los datos del cliente seleccionado
        vista.getTfRazonSocialCliente().setText(clienteSeleccionado.getRazonSocialCliente());
        vista.getTfRfcCliente().setText(clienteSeleccionado.getRfc());
        vista.getTfTelefonoCliente().setText(clienteSeleccionado.getTelefonoCliente());
        vista.getTfTipoCliente().setText(clienteSeleccionado.getTipo());
        vista.getTaDireccionCliente().setText(clienteSeleccionado.getDireccionCliente());
        
        // Cambiar el texto del botón a "Actualizar"
        vista.getBtnGuardarRegistroCliente().setText("Actualizar");
        
        // Mostrar el diálogo
        vista.getDialogRegistrarCliente().setTitle("Actualizar Cliente");
        mostrarDialogoRegistro();
        
        // Cambiar el listener temporalmente para actualizar
        vista.getBtnGuardarRegistroCliente().removeActionListener(vista.getBtnGuardarRegistroCliente().getActionListeners()[0]);
        vista.getBtnGuardarRegistroCliente().addActionListener(e -> {
            try {
                clienteSeleccionado.setRazonSocialCliente(vista.getTfRazonSocialCliente().getText());
                clienteSeleccionado.setRfc(vista.getTfRfcCliente().getText());
                clienteSeleccionado.setTelefonoCliente(vista.getTfTelefonoCliente().getText());
                clienteSeleccionado.setTipo(vista.getTfTipoCliente().getText());
                clienteSeleccionado.setDireccionCliente(vista.getTaDireccionCliente().getText());
                
                if (ClienteDAO.update(clienteSeleccionado)) {
                    JOptionPane.showMessageDialog(vista, "Cliente actualizado con éxito");
                    cerrarDialogoRegistro();
                    cargarClientes();
                    
                    // Restaurar el listener original
                    vista.getBtnGuardarRegistroCliente().removeActionListener(vista.getBtnGuardarRegistroCliente().getActionListeners()[0]);
                    vista.getBtnGuardarRegistroCliente().addActionListener(ev -> guardarCliente());
                    vista.getBtnGuardarRegistroCliente().setText("Guardar");
                    vista.getDialogRegistrarCliente().setTitle("Registrar Cliente");
                } else {
                    mostrarError("No se pudo actualizar el cliente");
                }
            } catch (SQLException ex) {
                mostrarError("Error al actualizar cliente: " + ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Datos inválidos: " + ex.getMessage());
            }
        });
    }
    
    private void eliminarCliente() {
        int selectedRow = vista.getTblClientes().getSelectedRow();
        if (selectedRow == -1) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro que desea eliminar este cliente?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                Cliente clienteAEliminar = listaClientes.get(selectedRow);
                if (ClienteDAO.delete(clienteAEliminar.getIdCliente())) {
                    JOptionPane.showMessageDialog(vista, "Cliente eliminado con éxito");
                    cargarClientes();
                } else {
                    mostrarError("No se pudo eliminar el cliente");
                }
            } catch (SQLException ex) {
                mostrarError("Error al eliminar cliente: " + ex.getMessage());
            }
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

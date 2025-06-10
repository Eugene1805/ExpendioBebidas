package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.vista.Clientes;
import java.awt.HeadlessException;
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
    private final Clientes vista;
    private final ClienteDAO modeloDAO;
    private boolean modoActualizacion = false;
    private Cliente clienteEnEdicion = null;
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
    
    private void restaurarListenerOriginal(){
        vista.getBtnGuardarRegistroCliente().removeActionListener(vista.getBtnGuardarRegistroCliente().getActionListeners()[0]);
        vista.getBtnGuardarRegistroCliente().addActionListener(ev -> guardarCliente());
        vista.getBtnGuardarRegistroCliente().setText("Guardar");
        vista.getDialogRegistrarCliente().setTitle("Registrar Cliente");        
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
        if (modoActualizacion && clienteEnEdicion != null) {
            clienteEnEdicion.setRazonSocialCliente(vista.getTfRazonSocialCliente().getText());
            clienteEnEdicion.setRfc(vista.getTfRfcCliente().getText());
            clienteEnEdicion.setTelefonoCliente(vista.getTfTelefonoCliente().getText());
            clienteEnEdicion.setTipo(vista.getTfTipoCliente().getText());
            clienteEnEdicion.setDireccionCliente(vista.getTaDireccionCliente().getText());

            if (ClienteDAO.update(clienteEnEdicion)) {
                JOptionPane.showMessageDialog(vista.getDialogRegistrarCliente(), "Cliente actualizado con éxito");
            } else {
                mostrarError("No se pudo actualizar el cliente");
            }
        } else {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setRazonSocialCliente(vista.getTfRazonSocialCliente().getText());
            nuevoCliente.setRfc(vista.getTfRfcCliente().getText());
            nuevoCliente.setTelefonoCliente(vista.getTfTelefonoCliente().getText());
            nuevoCliente.setTipo(vista.getTfTipoCliente().getText());
            nuevoCliente.setDireccionCliente(vista.getTaDireccionCliente().getText());

            if (modeloDAO.create(nuevoCliente)) {
                JOptionPane.showMessageDialog(vista.getDialogRegistrarCliente(), "Cliente registrado con éxito");
            } else {
                mostrarError("No se pudo registrar el cliente");
            }
        }

        cerrarDialogoRegistro();
        cargarClientes();
        restaurarEstadoRegistro();

    } catch (SQLException | HeadlessException ex) {
        mostrarError("Error al guardar cliente: " + ex.getMessage());
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

        // Obtener el cliente seleccionado
        clienteEnEdicion = listaClientes.get(selectedRow);
        modoActualizacion = true;

        // Llenar el formulario con los datos del cliente
        vista.getTfRazonSocialCliente().setText(clienteEnEdicion.getRazonSocialCliente());
        vista.getTfRfcCliente().setText(clienteEnEdicion.getRfc());
        vista.getTfTelefonoCliente().setText(clienteEnEdicion.getTelefonoCliente());
        vista.getTfTipoCliente().setText(clienteEnEdicion.getTipo());
        vista.getTaDireccionCliente().setText(clienteEnEdicion.getDireccionCliente());

        // Actualizar textos del botón y ventana
        vista.getBtnGuardarRegistroCliente().setText("Actualizar");
        vista.getDialogRegistrarCliente().setTitle("Actualizar Cliente");

        // Mostrar el diálogo centrado
        vista.getDialogRegistrarCliente().pack();
        vista.getDialogRegistrarCliente().setLocationRelativeTo(vista);
        vista.getDialogRegistrarCliente().setVisible(true);
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
    
    private void restaurarEstadoRegistro() {
        modoActualizacion = false;
        clienteEnEdicion = null;
        vista.getBtnGuardarRegistroCliente().setText("Guardar");
        vista.getDialogRegistrarCliente().setTitle("Registrar Cliente");
    }

    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.PromocionDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Promocion;
import expendiobebidas.modelo.pojo.PromocionBebida;
import expendiobebidas.vista.Bebidas;
import java.awt.HeadlessException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author eugen
 */
public class BebidaController {
    private final Bebidas vista;
    private final BebidaDAO modeloDAO;
    private final PromocionDAO promocionDAO;
    
    private List<Bebida> listaBebidas;
    private List<Promocion> listaPromociones;
    
    public BebidaController(Bebidas vista) {
        this.vista = vista;
        this.modeloDAO = new BebidaDAO();
        this.promocionDAO = new PromocionDAO();
        this.listaBebidas = new ArrayList<>();
        this.listaPromociones = new ArrayList<>();
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
        for (var listener : vista.getBtnGuardarRegistroBebida().getActionListeners()) {
            vista.getBtnGuardarRegistroBebida().removeActionListener(listener);
        }
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
    
    public void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTblBebidas().getModel();
        model.setRowCount(0); // Limpiar la tabla

        for (Bebida bebida : listaBebidas) {
            String descripcionPromociones = "";

            try {
                List<Promocion> promociones = PromocionDAO.obtenerPromocionesPorBebida(bebida.getIdBebida());
                descripcionPromociones = promociones.isEmpty() ? "Sin promoción" :
                    String.join(", ", promociones.stream().map(Promocion::getDescripcion).toList());
            } catch (SQLException e) {
                descripcionPromociones = "Error";
            }

            Object[] row = {
                bebida.getNombre(),
                bebida.getPrecio(),
                bebida.getStockActual(),
                bebida.getDescripcion(),
                descripcionPromociones
            };
            model.addRow(row);
        }
    }

    
    private void mostrarDialogoRegistro() {
        limpiarCamposDialogo();
        cargarPromociones();
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
            if (!validarDatosBebida()) {
                return; // Si la validación falla, se detiene la ejecución
            }
            Bebida nuevaBebida = new Bebida();
            nuevaBebida.setNombre(vista.getTfNombreBebida().getText());
            Float precioFloat = (Float) vista.getSpPrecioBebida().getValue();
            nuevaBebida.setPrecio(BigDecimal.valueOf(precioFloat.doubleValue()));
            nuevaBebida.setStockMinimo((Integer)vista.getSpStockMinimo().getValue());
            nuevaBebida.setStockActual((Integer)vista.getSpStockActual().getValue());
            nuevaBebida.setDescripcion(vista.getTaDescripcionBebida().getText());
            
            if (modeloDAO.create(nuevaBebida)) {
                JOptionPane.showMessageDialog(vista.getDialogRegistrarBebida(), "Bebida registrada con éxito");
                cerrarDialogoRegistro();
                cargarBebidas();
            } else {
                mostrarError("No se pudo registrar la bebida");
            }
            if(vista.getCbPromocion().getSelectedItem() != null){
                PromocionBebida promocionBebida = new PromocionBebida();
                promocionBebida.setBebida(nuevaBebida);
                promocionBebida.setPromocion((Promocion)vista.getCbPromocion().getSelectedItem());
                if(modeloDAO.registrarPromocion(promocionBebida)){
                    JOptionPane.showMessageDialog(vista.getDialogRegistrarBebida(), "Promocion asociada con éxito");
                    cargarBebidas();
                }
            }
            
        } catch (SQLException ex) {
            mostrarError("Error al guardar bebida: " + ex.getMessage());
        } catch (HeadlessException ex) {
            mostrarError("Datos inválidos: " + ex.getMessage());
        }
    }
    
    private void cargarPromociones(){
        try {
            listaPromociones = promocionDAO.readAll();
            DefaultComboBoxModel<Promocion> promocionesModel = new DefaultComboBoxModel<>();
            listaPromociones.forEach(promocionesModel :: addElement);
            vista.getCbPromocion().setModel(promocionesModel);
        } catch (SQLException ex) {
            Logger.getLogger(BebidaController.class.getName()).log(Level.SEVERE, null, ex);
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
                Number precioNumber = (Number) vista.getSpPrecioBebida().getValue();
                bebidaSeleccionada.setPrecio(BigDecimal.valueOf(precioNumber.doubleValue()));
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
                if(vista.getCbPromocion().getSelectedItem() != null){
                    PromocionBebida promocionBebida = new PromocionBebida();
                    promocionBebida.setBebida(bebidaSeleccionada);
                    promocionBebida.setPromocion((Promocion)vista.getCbPromocion().getSelectedItem());
                    if(modeloDAO.registrarPromocion(promocionBebida)){
                        JOptionPane.showMessageDialog(vista.getDialogRegistrarBebida(), "Promocion asociada con éxito");
                        cargarBebidas();
                    }
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

        // Actualizar lista por seguridad (opcional pero recomendable)
        try {
            listaBebidas = BebidaDAO.readAll();
        } catch (SQLException e) {
            mostrarError("Error al sincronizar lista antes de eliminar: " + e.getMessage());
            return;
        }

        if (selectedRow >= listaBebidas.size()) {
            mostrarError("La bebida seleccionada ya no existe.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro que desea eliminar esta bebida?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                Bebida bebidaAEliminar = listaBebidas.get(selectedRow);

                // ✅ Primero eliminar asociaciones en la tabla promocion_bebida
                modeloDAO.eliminarPromocionesAsociadas(bebidaAEliminar.getIdBebida());

                // ✅ Luego eliminar la bebida
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

    private boolean validarDatosBebida() {
        String nombre = vista.getTfNombreBebida().getText().trim();
        String descripcion = vista.getTaDescripcionBebida().getText().trim();
        
        BigDecimal precio;
        Integer stockMinimo;
        Integer stockActual;

        try {
            precio = BigDecimal.valueOf(((Number) vista.getSpPrecioBebida().getValue()).doubleValue());
            stockMinimo = (Integer) vista.getSpStockMinimo().getValue();
            stockActual = (Integer) vista.getSpStockActual().getValue();
        } catch (ClassCastException | NullPointerException e) {
            mostrarError("Asegúrese de que el precio y el stock sean valores numéricos válidos.");
            return false;
        }

        if (nombre.isEmpty()) {
            mostrarError("El nombre de la bebida es obligatorio.");
            return false;
        }
        
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarError("El precio de la bebida debe ser un valor positivo.");
            return false;
        }
        
        if (stockMinimo < 0) {
            mostrarError("El stock mínimo no puede ser negativo.");
            return false;
        }
        
        if (stockActual < 0) {
            mostrarError("El stock actual no puede ser negativo.");
            return false;
        }
        
        if (stockActual < stockMinimo) {
            mostrarError("El stock actual no puede ser menor que el stock mínimo.");
            return false;
        }
        
        if (descripcion.isEmpty()) {
            mostrarError("La descripción de la bebida es obligatoria.");
            return false;
        }
        
        return true; // Todos los datos son válidos
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

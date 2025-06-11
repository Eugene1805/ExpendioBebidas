package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.PromocionDAO;
import expendiobebidas.modelo.pojo.Promocion;
import expendiobebidas.vista.Promociones;
import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PromocionController {
    private final Promociones vista;
    private final PromocionDAO modeloDAO;
    private List<Promocion> listaPromociones;
    private final DateTimeFormatter dateFormatter;
    
    public PromocionController(Promociones vista) {
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.vista = vista;
        this.modeloDAO = new PromocionDAO();
        this.listaPromociones = new ArrayList<>();
        
        configurarRenderizadorTabla();
        configurarListeners();
        cargarPromociones();
    }
    
    private void configurarRenderizadorTabla() {
        vista.getTblPromociones().getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Float) {
                    value = String.format("%.2f%%", (Float)value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, 
                        hasFocus, row, column);
            }
        });
    }
    
    private void configurarListeners() {
        vista.getBtnRegistrarPromocion().addActionListener(e -> mostrarDialogoRegistro());
        vista.getBtnGuardarPromocion().addActionListener(e -> guardarPromocion());
        vista.getBtnCancelarPromocion().addActionListener(e -> cerrarDialogoRegistro());
        vista.getBtnActualizarPromocion().addActionListener(e -> actualizarPromocion());
        vista.getBtnElimarPromoción().addActionListener(e -> eliminarPromocion());
        
        vista.getTblPromociones().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                habilitarBotonesEdicion();
            }
        });
    }
    
    private void restaurarListenerOriginal(){
        vista.getBtnGuardarPromocion().removeActionListener(vista.getBtnGuardarPromocion().getActionListeners()[0]);
        vista.getBtnGuardarPromocion().addActionListener(ev -> guardarPromocion());
        vista.getBtnGuardarPromocion().setText("Guardar");
        vista.getDialogRegistrarPromociones().setTitle("Registrar Promoción");
    }
    
    private void cargarPromociones() {
        try {
            listaPromociones = PromocionDAO.readAll();
            actualizarTabla();
        } catch (SQLException ex) {
            mostrarError("Error al cargar promociones: " + ex.getMessage());
        }
    }
    
    private void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTblPromociones().getModel();
        model.setRowCount(0);
        
        for (Promocion promocion : listaPromociones) {
            Object[] row = {
                promocion.getDescripcion(),
                promocion.getDescuento(),
                promocion.getFechaInicio().format(dateFormatter),
                promocion.getFechaFin().format(dateFormatter)
            };
            model.addRow(row);
        }
    }
    
    private void mostrarDialogoRegistro() {
        limpiarCamposDialogo();
        vista.getDialogRegistrarPromociones().pack();
        vista.getDialogRegistrarPromociones().setLocationRelativeTo(vista);
        vista.getDialogRegistrarPromociones().setVisible(true);
    }
    
    private void cerrarDialogoRegistro() {
        vista.getDialogRegistrarPromociones().setVisible(false);
    }
    
    private void limpiarCamposDialogo() {
        vista.getTaDescripcionPromocion().setText("");
        vista.getSpDescuentoPromocion().setValue(0.0f);
        vista.getSpFechaInicioPromocion().setValue(new Date());
        vista.getSpFechaFinPromocion().setValue(new Date());
    }
    
    private void guardarPromocion() {
        try {
            if (!validarDatosPromocion()) {
                return;
            }
            
            Promocion nuevaPromocion = new Promocion();
            nuevaPromocion.setDescripcion(vista.getTaDescripcionPromocion().getText().trim());
            nuevaPromocion.setDescuento((BigDecimal) vista.getSpDescuentoPromocion().getValue());
            
            Date fechaInicio = (Date)vista.getSpFechaInicioPromocion().getValue();
            nuevaPromocion.setFechaInicio(convertToLocalDate(fechaInicio));
            
            Date fechaFin = (Date)vista.getSpFechaFinPromocion().getValue();
            nuevaPromocion.setFechaFin(convertToLocalDate(fechaFin));
            
            if (modeloDAO.create(nuevaPromocion)) {
                JOptionPane.showMessageDialog(vista.getDialogRegistrarPromociones(), "Promoción registrada con éxito");
                cerrarDialogoRegistro();
                cargarPromociones();
            } else {
                mostrarError("No se pudo registrar la promoción");
            }
        } catch (SQLException ex) {
            mostrarError("Error al guardar promoción: " + ex.getMessage());
        }
    }
    
    // Método para convertir Date a LocalDate
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    
    // Método para convertir LocalDate a Date
    private Date convertToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
    
    private boolean validarDatosPromocion() {
        String descripcion = vista.getTaDescripcionPromocion().getText().trim();
        float descuento = (Float)vista.getSpDescuentoPromocion().getValue();
        Date fechaInicio = (Date)vista.getSpFechaInicioPromocion().getValue();
        Date fechaFin = (Date)vista.getSpFechaFinPromocion().getValue();
        
        if (descripcion.isEmpty()) {
            mostrarError("La descripción de la promoción es obligatoria");
            return false;
        }
        
        if (descuento <= 0 || descuento > 100) {
            mostrarError("El descuento debe ser un valor entre 0.1 y 100");
            return false;
        }
        
        if (fechaInicio.after(fechaFin)) {
            mostrarError("La fecha de inicio no puede ser posterior a la fecha de fin");
            return false;
        }
        
        return true;
    }
    
    private void habilitarBotonesEdicion() {
        int selectedRow = vista.getTblPromociones().getSelectedRow();
        boolean habilitar = selectedRow != -1;
        
        vista.getBtnActualizarPromocion().setEnabled(habilitar);
        vista.getBtnElimarPromoción().setEnabled(habilitar);
    }
    
    private void actualizarPromocion() {
        int selectedRow = vista.getTblPromociones().getSelectedRow();
        if (selectedRow == -1) return;
        
        Promocion promocionSeleccionada = listaPromociones.get(selectedRow);
        
        // Llenar el diálogo con los datos de la promoción seleccionada
        vista.getTaDescripcionPromocion().setText(promocionSeleccionada.getDescripcion());
        vista.getSpDescuentoPromocion().setValue(promocionSeleccionada.getDescuento());
        
        // Convertir LocalDate a Date para mostrar en los spinners
        vista.getSpFechaInicioPromocion().setValue(convertToDate(promocionSeleccionada.getFechaInicio()));
        vista.getSpFechaFinPromocion().setValue(convertToDate(promocionSeleccionada.getFechaFin()));
        
        // Cambiar el texto del botón a "Actualizar"
        vista.getBtnGuardarPromocion().setText("Actualizar");
        
        // Mostrar el diálogo
        vista.getDialogRegistrarPromociones().setTitle("Actualizar Promoción");
        vista.getDialogRegistrarPromociones().pack();
        vista.getDialogRegistrarPromociones().setLocationRelativeTo(vista);
        vista.getDialogRegistrarPromociones().setVisible(true);
        
        // Cambiar el listener temporalmente para actualizar
        vista.getBtnGuardarPromocion().removeActionListener(vista.getBtnGuardarPromocion().getActionListeners()[0]);
        vista.getBtnGuardarPromocion().addActionListener(e -> {
            try {
                if (!validarDatosPromocion()) {
                    return;
                }
                
                promocionSeleccionada.setDescripcion(vista.getTaDescripcionPromocion().getText().trim());
                promocionSeleccionada.setDescuento((BigDecimal)vista.getSpDescuentoPromocion().getValue());
                
                Date fechaInicio = (Date)vista.getSpFechaInicioPromocion().getValue();
                promocionSeleccionada.setFechaInicio(convertToLocalDate(fechaInicio));
                
                Date fechaFin = (Date)vista.getSpFechaFinPromocion().getValue();
                promocionSeleccionada.setFechaFin(convertToLocalDate(fechaFin));
                
                if (PromocionDAO.update(promocionSeleccionada)) {
                    JOptionPane.showMessageDialog(vista.getDialogRegistrarPromociones(), "Promoción actualizada con éxito");
                    cerrarDialogoRegistro();
                    cargarPromociones();
                    restaurarListenerOriginal();
                } else {
                    mostrarError("No se pudo actualizar la promoción");
                }
            } catch (SQLException ex) {
                mostrarError("Error al actualizar promoción: " + ex.getMessage());
            }
        });
    }
    
    private void eliminarPromocion() {
        int selectedRow = vista.getTblPromociones().getSelectedRow();
        if (selectedRow == -1) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro que desea eliminar esta promoción?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                Promocion promocionAEliminar = listaPromociones.get(selectedRow);
                if (PromocionDAO.delete(promocionAEliminar.getIdPromocion())) {
                    JOptionPane.showMessageDialog(vista.getDialogRegistrarPromociones(), "Promoción eliminada con éxito");
                    cargarPromociones();
                } else {
                    mostrarError("No se pudo eliminar la promoción");
                }
            } catch (SQLException ex) {
                mostrarError("Error al eliminar promoción: " + ex.getMessage());
            }
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ReporteStockMinimo;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author meler
 */
public class ReporteStockMinimoController {

    public static void cargarReporteStockMinimo(JTable tabla) {
        try {
            List<ReporteStockMinimo> lista = ReportesDAO.obtenerStockMinimo();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);

            for (ReporteStockMinimo reporte : lista) {
                Object[] fila = {
                    reporte.getNombre(),
                    reporte.getStockActual(),
                    reporte.getStockMinimo()
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error al obtener el reporte de stock mínimo:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


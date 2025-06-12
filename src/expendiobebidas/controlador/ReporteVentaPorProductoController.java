package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ReporteVentaPorProducto;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author meler
 */

public class ReporteVentaPorProductoController {

    public static void cargarVentasPorProducto(JTable tabla) {
        try {
            List<ReporteVentaPorProducto> lista = ReportesDAO.obtenerVentasPorProducto();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0); // Limpiar la tabla

            for (ReporteVentaPorProducto reporte : lista) {
                Object[] fila = {
                    reporte.getBebida(),
                    reporte.getFolioVenta(),
                    reporte.getCantidad(),
                    reporte.getSubtotal()
                };
                modelo.addRow(fila);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar ventas por producto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

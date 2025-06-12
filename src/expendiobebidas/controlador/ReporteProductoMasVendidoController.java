package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ProductoMasVendido;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author meler
 */
public class ReporteProductoMasVendidoController {

    public static void cargarProductosMasVendidos(JTable tabla) {
        try {
            List<ProductoMasVendido> lista = ReportesDAO.obtenerProductosMasVendidos();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);

            for (ProductoMasVendido producto : lista) {
                Object[] fila = {
                    producto.getNombre(),
                    producto.getTotalVendido()
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar los productos más vendidos:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ProductoMenosVendido;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author meler
 */

public class ReporteProductoMenosVendidoController {

    public static void cargarProductosMenosVendidos(JTable tabla) {
        try {
            List<ProductoMenosVendido> lista = ReportesDAO.obtenerProductosMenosVendidos();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);

            for (ProductoMenosVendido producto : lista) {
                Object[] fila = {
                    producto.getProducto(),
                    producto.getTotalVendidos()
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar los productos menos vendidos:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


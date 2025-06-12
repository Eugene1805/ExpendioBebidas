package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ProductoMasVendidoPorCliente;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;


/**
 *
 * @author meler
 */

public class ReporteClienteMasVendidoController {

    public static void buscarProductoMasVendido(JTable tabla, int idCliente) {
        try {
            List<ProductoMasVendidoPorCliente> lista = ReportesDAO.obtenerProductoMasVendidoPorCliente(idCliente);
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);

            for (ProductoMasVendidoPorCliente producto : lista) {
                Object[] fila = {
                    producto.getBebida(),
                    producto.getTotalVendidos()
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar el producto más vendido:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

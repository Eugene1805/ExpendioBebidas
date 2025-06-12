package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ProductoNoVendido;
import expendiobebidas.modelo.pojo.Cliente;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

/**
 *
 * @author meler
 */


public class ReporteClienteNoVendidoController {

    public static void buscarProductosNoVendidos(JTable tabla, int idCliente) {
        try {
            List<ProductoNoVendido> lista = ReportesDAO.obtenerProductosNoVendidosPorCliente(idCliente);
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);

            for (ProductoNoVendido p : lista) {
                Object[] fila = { p.getNombre(), 0 }; // Total vendidos es 0
                modelo.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al buscar productos no vendidos:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

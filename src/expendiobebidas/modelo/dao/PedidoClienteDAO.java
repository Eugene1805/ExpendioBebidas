package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.OrdenPedido;
import expendiobebidas.modelo.pojo.PedidoCliente;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
/**
 *
 * @author Nash
 */
public class PedidoClienteDAO {

    public static boolean registrarPedidoCompleto(PedidoCliente pedido, List<OrdenPedido> ordenes){
                Connection connection = null;
        try {
            connection = Conexion.abrirConexion();
            if (connection == null) return false;
            
            connection.setAutoCommit(false);

            String pedidoQuery = "INSERT INTO pedido_cliente (idpedido_cliente, fecha_pedido_cliente, descuento_pedido_cliente, total_pedido_cliente, cliente_idcliente) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement psPedido = connection.prepareStatement(pedidoQuery)) {
                // Aquí usamos el ID que se genera en el controlador.
                psPedido.setInt(1, pedido.getIdPedidoCliente()); 
                psPedido.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
                psPedido.setBigDecimal(3, pedido.getDescuentoPedidoCliente());
                psPedido.setBigDecimal(4, pedido.getTotalPedidoCliente());
                psPedido.setInt(5, pedido.getCliente().getIdCliente());
                
                int affectedRows = psPedido.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Fallo al insertar el pedido del cliente.");
                }
            }

            String ordenQuery = "INSERT INTO orden_pedido (pedido_cliente_idpedido_cliente, bebida_idbebida, subtotal_orden_pedido, cantidad_orden_pedido) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psOrden = connection.prepareStatement(ordenQuery)) {
                for (OrdenPedido orden : ordenes) {
                    psOrden.setInt(1, pedido.getIdPedidoCliente()); // ID del pedido que acabamos de insertar.
                    psOrden.setInt(2, orden.getBebida().getIdBebida());
                    psOrden.setDouble(3, orden.getSubtotalOrdenPedido());
                    psOrden.setInt(4, orden.getCantidadOrdenPedido());
                    psOrden.addBatch();
                }
                psOrden.executeBatch();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static int obtenerUltimoIdPedido() throws SQLException {
        int ultimoId = 0;
        String sql = "SELECT MAX(idpedido_cliente) FROM pedido_cliente";
        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                ultimoId = rs.getInt(1);
            }
        }
        return ultimoId;
    }
    
}

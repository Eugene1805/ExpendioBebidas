package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.DetallePedido;
import expendiobebidas.modelo.pojo.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;



public class PedidoProveedorDAO {


    public static boolean registrarPedidoCompleto(Pedido pedido, List<DetallePedido> detalles) throws SQLException{
        Connection connection = null;
        try {
            connection = Conexion.abrirConexion();
            connection.setAutoCommit(false);
            
            // 1. Insertar en la tabla 'pedido'
            String pedidoQuery = "INSERT INTO pedido (estado_pedido, fecha_pedido, total_pedido, proveedor_idproveedor) " +
                               "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psPedido = connection.prepareStatement(pedidoQuery, Statement.RETURN_GENERATED_KEYS)) {
                psPedido.setString(1, "pendiente"); // O el estado que desees
                psPedido.setDate(2, java.sql.Date.valueOf(pedido.getFechaPedido()));
                psPedido.setDouble(3, pedido.getTotal());
                psPedido.setInt(4, pedido.getProveedor().getIdProveedor());
                
                int affectedRows = psPedido.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("No se pudo insertar el pedido, ninguna fila afectada.");
                }
                
                // 2. Obtener el ID del pedido recién insertado
                try (ResultSet generatedKeys = psPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setIdPedido(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("No se pudo obtener el ID del pedido.");
                    }
                }
            }
            
            // 3. Insertar en la tabla 'detalle_pedido'
            String detalleQuery = "INSERT INTO detalle_pedido (pedido_idpedido, bebida_idbebida, subtotal_detalle_pedido, cantidad_detalle_pedido) " +
                                "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psDetalle = connection.prepareStatement(detalleQuery)) {
                for (DetallePedido detalle : detalles) {
                    psDetalle.setInt(1, pedido.getIdPedido());
                    psDetalle.setInt(2, detalle.getBebida().getIdBebida());
                    psDetalle.setDouble(3, detalle.getSubtotal());
                    psDetalle.setInt(4, detalle.getCantidad());
                    psDetalle.addBatch(); // Agregamos la instrucción al lote
                }
                psDetalle.executeBatch(); // Ejecutamos todas las inserciones juntas
            }

            // Si todo fue bien, confirmamos la transacción
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    // Si algo falló, revertimos
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Restaurar modo autocommit
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
           
    }

}

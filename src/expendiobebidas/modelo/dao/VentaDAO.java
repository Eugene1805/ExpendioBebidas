package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.DetalleVenta;
import expendiobebidas.modelo.pojo.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author meler
 */
public class VentaDAO {
    // (El método obtenerUltimoNumeroFolio no cambia, se queda como está)
    public int obtenerUltimoNumeroFolio(int anioActual) throws SQLException {
        String query = "SELECT COALESCE(MAX(CAST(SUBSTRING(folio_venta, 9) AS UNSIGNED)), 0) " +
                       "FROM venta WHERE folio_venta LIKE ?";
        int ultimoNumero = 0;
        String patronLike = String.format("VENT-%02d-%%", anioActual);
        try (Connection connection = Conexion.abrirConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, patronLike);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ultimoNumero = rs.getInt(1);
                }
            }
        }
        return ultimoNumero;
    }

    /**
     * Registra una venta completa (cabecera y detalles) dentro de una transacción.
     * @param venta El objeto Venta a registrar. Debe contener un objeto LocalDate.
     * @param detalles La lista de productos de la venta.
     * @return true si la transacción fue exitosa.
     * @throws SQLException Si ocurre un error y la transacción es revertida.
     */
    public boolean registrarVentaCompleta(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        Connection connection = null;
        try {
            connection = Conexion.abrirConexion();
            connection.setAutoCommit(false); // Iniciar Transacción

            // 1. Insertar la Venta principal
            String ventaQuery = "INSERT INTO venta (fecha_venta, descuento_venta, total_venta, cliente_idcliente, folio_venta) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psVenta = connection.prepareStatement(ventaQuery, Statement.RETURN_GENERATED_KEYS)) {
                // CAMBIO CLAVE: Usar setDate con la conversión de LocalDate a java.sql.Date
                psVenta.setDate(1, java.sql.Date.valueOf(venta.getFecha())); 
                psVenta.setBigDecimal(2, venta.getDescuento());
                psVenta.setBigDecimal(3, venta.getTotal());
                psVenta.setInt(4, venta.getCliente().getIdCliente());
                psVenta.setString(5, venta.getFolio());
                psVenta.executeUpdate();

                try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setIdVenta(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Fallo al crear la venta, no se obtuvo ID.");
                    }
                }
            }

            // 2. Insertar los Detalles
            String detalleQuery = "INSERT INTO detalle_venta (venta_idventa, bebida_idbebida, subtotal_detalle_venta, "
                    + "cantidad_detalle_venta, precio_momento_detalle_venta) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psDetalle = connection.prepareStatement(detalleQuery)) {
                for (DetalleVenta detalle : detalles) {
                    psDetalle.setInt(1, venta.getIdVenta());
                    psDetalle.setInt(2, detalle.getBebida().getIdBebida());
                    psDetalle.setDouble(3, detalle.getSubtotal());
                    psDetalle.setInt(4, detalle.getCantidad());
                    psDetalle.setBigDecimal(5, detalle.getPrecioMomento());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            connection.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Revertir cambios en caso de error
            }
            throw e; // Relanzar la excepción para notificar al controlador
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }
}

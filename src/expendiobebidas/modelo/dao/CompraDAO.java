package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.DetalleCompra;
import expendiobebidas.modelo.pojo.Compra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author eugen
 */
public class CompraDAO {

    /**
     * Obtiene el número consecutivo del último folio de compra registrado para un año específico.
     * El formato esperado es 'COMP-YY-NNN'.
     * @param anioActual El año de dos dígitos (ej. 24 para 2024).
     * @return El último número de folio para ese año. Si no hay, devuelve 0.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public int obtenerUltimoNumeroFolio(int anioActual) throws SQLException {
        // CORRECCIÓN: Apuntar a la tabla 'compra' y buscar el SUBSTRING correcto.
        // El folio es 'COMP-YYYY-NNN'. El número NNN empieza en la posición 12.
        // O si tu formato es 'COMP-YY-NNN', empieza en la posición 9. Usaremos YY.
        String query = "SELECT COALESCE(MAX(CAST(SUBSTRING(folio, 9) AS UNSIGNED)), 0) " +
                       "FROM compra WHERE folio LIKE ?";
        
        int ultimoNumero = 0;
        String patronLike = String.format("COMP-%02d-%%", anioActual);

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
     * Registra una compra completa (cabecera y detalles) usando una transacción.
     * El trigger 'actualizar_stock_compra' se encargará de sumar el stock.
     * @param compra El objeto Compra a registrar.
     * @param detalles La lista de detalles de la compra.
     * @return true si la transacción fue exitosa.
     * @throws SQLException Si ocurre un error y la transacción es revertida.
     */
    public boolean registrarCompraCompleta(Compra compra, List<DetalleCompra> detalles) throws SQLException {
        Connection connection = null;
        try {
            connection = Conexion.abrirConexion();
            connection.setAutoCommit(false); // Iniciar Transacción

            // 1. Insertar la cabecera de la Compra
            String compraQuery = "INSERT INTO compra (folio, fecha_compra, total_compra) VALUES (?, ?, ?)";
            try (PreparedStatement psCompra = connection.prepareStatement(compraQuery)) {
                psCompra.setString(1, compra.getFolio());
                psCompra.setDate(2, java.sql.Date.valueOf(compra.getFechaCompra()));
                psCompra.setBigDecimal(3, compra.getTotalCompra());
                psCompra.executeUpdate();
            }

            // 2. Insertar los Detalles de la Compra
            String detalleQuery = "INSERT INTO detalle_compra (compra_folio, bebida_idbebida, cantidad, subtotal) "
                                 + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement psDetalle = connection.prepareStatement(detalleQuery)) {
                for (DetalleCompra detalle : detalles) {
                    psDetalle.setString(1, detalle.getCompra().getFolio());
                    psDetalle.setInt(2, detalle.getBebida().getIdBebida());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setDouble(4, detalle.getSubtotal());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            connection.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Revertir en caso de error
            }
            throw e; // Relanzar la excepción
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }
    
}

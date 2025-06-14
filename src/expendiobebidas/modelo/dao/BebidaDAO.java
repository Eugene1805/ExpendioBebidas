package expendiobebidas.modelo.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.PromocionBebida;
import java.util.ArrayList;


/**
 *
 * @author eugen
 */
public class BebidaDAO {
    public boolean create(Bebida bebida) throws SQLException{
        if(bebida == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "INSERT INTO bebida (nombre, descripcion, stock_minimo, precio, stock_actual) VALUES "
                + "(?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, bebida.getNombre());
        ps.setString(2, bebida.getDescripcion());
        ps.setInt(3, bebida.getStockMinimo());
        ps.setBigDecimal(4, bebida.getPrecio());
        ps.setInt(5, bebida.getStockActual());
        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) bebida.setIdBebida(generatedKeys.getInt(1));
            connection.close();
            return true;
        }else{
            connection.close();
            return false;
        }
    }
    
    public static Bebida read(int id)throws SQLException{
        Bebida bebida = new Bebida();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idbebida, nombre, descripcion, stock_minimo, stock_actual, precio FROM bebida "
                + "WHERE idbebida = ?";
        PreparedStatement ps = connection.prepareCall(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            bebida.setIdBebida(rs.getInt("idbebida"));
            bebida.setNombre(rs.getString("nombre"));
            bebida.setDescripcion(rs.getString("descripcion"));
            bebida.setPrecio(rs.getBigDecimal("precio"));
            bebida.setStockActual(rs.getInt("stock_actual"));
            bebida.setStockMinimo(rs.getInt("stock_minimo"));
        }
        connection.close();
        return bebida;
    }
    
    public static boolean update(Bebida bebida) throws SQLException{
        if(bebida == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "UPDATE bebida SET nombre = ?, descripcion = ?, stock_minimo = ?, precio = ? "
                + "WHERE idbebida = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, bebida.getNombre());
        ps.setString(2, bebida.getDescripcion());
        ps.setInt(3, bebida.getStockMinimo());
        ps.setBigDecimal(4, bebida.getPrecio());
        ps.setInt(5, bebida.getIdBebida());
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    }
    
    public static boolean delete(int id) throws SQLException{
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "DELETE FROM bebida WHERE idbebida = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    } 
    
    public static ArrayList<Bebida> readAll() throws SQLException{
        ArrayList<Bebida> bebidas = new ArrayList<>();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idbebida, nombre, descripcion, stock_minimo, stock_actual, precio FROM bebida";
        PreparedStatement ps = connection.prepareCall(query);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Bebida bebida = new Bebida();
            bebida.setIdBebida(rs.getInt("idbebida"));
            bebida.setNombre(rs.getString("nombre"));
            bebida.setDescripcion(rs.getString("descripcion"));
            bebida.setPrecio(rs.getBigDecimal("precio"));
            bebida.setStockActual(rs.getInt("stock_actual"));
            bebida.setStockMinimo(rs.getInt("stock_minimo"));
            bebidas.add(bebida);
        }
        connection.close();
        return bebidas;
    }
    
    public static ArrayList<Bebida> readAllStockBajo() throws SQLException{
        ArrayList<Bebida> bebidas = new ArrayList<>();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw  new SQLException();
        String query = "SELECT idbebida, nombre, descripcion, stock_minimo, precio, stock_actual FROM bebida WHERE stock_actual < stock_minimo";
        PreparedStatement ps = connection.prepareCall(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Bebida bebida = new Bebida();
            bebida.setIdBebida(rs.getInt("idbebida"));
            bebida.setNombre(rs.getString("nombre"));
            bebida.setDescripcion(rs.getString("descripcion"));
            bebida.setStockMinimo(rs.getInt("stock_minimo"));
            bebida.setPrecio(rs.getBigDecimal("precio"));
            bebida.setStockActual(rs.getInt("stock_actual"));
            bebidas.add(bebida);            
        }
        connection.close();
        return  bebidas;
    }
    
    public boolean registrarPromocion(PromocionBebida bebidaPromocion) throws SQLException{
        if(bebidaPromocion == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "INSERT INTO promocion_bebida (promocion_idpromocion, bebida_idbebida) VALUES "
                + "(?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, bebidaPromocion.getPromocion().getIdPromocion());
        ps.setInt(2, bebidaPromocion.getBebida().getIdBebida());
        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            connection.close();
            return true;
        }else{
            connection.close();
            return false;
        }
    }
    
    public boolean eliminarPromocionesAsociadas(int idBebida) throws SQLException {
        Connection conn = Conexion.abrirConexion();
        if (conn == null) throw new SQLException("No se pudo abrir la conexión");

        String query = "DELETE FROM promocion_bebida WHERE bebida_idbebida = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idBebida);
            ps.executeUpdate();
            return true;
        } finally {
            conn.close();
        }
    }
    
        public boolean eliminarDetallesPedidoAsociados(int idBebida) throws SQLException {
        Connection conn = Conexion.abrirConexion();
        if (conn == null) throw new SQLException("No se pudo abrir la conexión");

        String query = "DELETE FROM detalle_pedido WHERE bebida_idbebida = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idBebida);
            ps.executeUpdate();
            return true;
        } finally {
            conn.close();
        }
    }
        
        public boolean eliminarDetallesVentaAsociados(int idBebida) throws SQLException {
            Connection conn = Conexion.abrirConexion();
            if (conn == null) throw new SQLException("No se pudo abrir la conexión");

            String query = "DELETE FROM detalle_venta WHERE bebida_idbebida = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idBebida);
                ps.executeUpdate();
                return true;
            } finally {
                conn.close();
            }
        }

        public boolean eliminarDetallesCompraAsociados(int idBebida) throws SQLException {
            Connection conn = Conexion.abrirConexion();
            if (conn == null) throw new SQLException("No se pudo abrir la conexión");

            String query = "DELETE FROM detalle_compra WHERE bebida_idbebida = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idBebida);
                ps.executeUpdate();
                return true;
            } finally {
                conn.close();
            }
        }



}

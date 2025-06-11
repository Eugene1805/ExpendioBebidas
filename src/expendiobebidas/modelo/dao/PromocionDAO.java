package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.Promocion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author eugen
 */
public class PromocionDAO {
    public boolean create(Promocion promocion) throws SQLException{
        if(promocion == null) return false;
        
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "INSERT INTO promocion (descripcion_promocion, descuento_promocion, "
                + "fecha_inicio_promocion, fecha_fin_promocion) VALUES (?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, promocion.getDescripcion());
        ps.setBigDecimal(2, promocion.getDescuento());
        ps.setDate(3, java.sql.Date.valueOf(promocion.getFechaInicio()));
        ps.setDate(4, java.sql.Date.valueOf(promocion.getFechaFin()));
        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) promocion.setIdPromocion(generatedKeys.getInt(1));
            connection.close();
            return true;
        }else{
            connection.close();
            return false;
        }
    }
    
    public static Promocion read(int id)throws SQLException{
        Promocion promocion = new Promocion();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idpromocion, descripcion_promocion, descuento_promocion, "
                + "fecha_inicio_promocion, fecha_fin_promocion FROM promocion WHERE idpromocion = ?";
        PreparedStatement ps = connection.prepareCall(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            promocion.setIdPromocion(rs.getInt("idpromocion"));
            promocion.setDescripcion(rs.getString("descripcion_promocion"));
            promocion.setDescuento(rs.getBigDecimal("descuento_promocion"));
            promocion.setFechaInicio(rs.getDate("fecha_inicio_promocion").toLocalDate());
            promocion.setFechaFin(rs.getDate("fecha_fin_promocion").toLocalDate());
        }
        connection.close();
        return promocion;
    }
    
    public static boolean update(Promocion promocion) throws SQLException{
        if(promocion == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "UPDATE promocion SET descripcion_promocion = ?, descuento_promocion = ?, "
                + "fecha_inicio_promocion = ?, fecha_fin_promocion = ? WHERE idpromocion = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, promocion.getDescripcion());
        ps.setBigDecimal(2, promocion.getDescuento());
        ps.setDate(3, java.sql.Date.valueOf(promocion.getFechaInicio()));
        ps.setDate(4, java.sql.Date.valueOf(promocion.getFechaFin()));
        ps.setInt(5, promocion.getIdPromocion());
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    }
    
    public static boolean delete(int id) throws SQLException{
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "DELETE FROM promocion WHERE idpromocion = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    } 
    
    public static ArrayList<Promocion> readAll() throws SQLException{
        ArrayList<Promocion> promociones = new ArrayList<>();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idpromocion, descripcion_promocion, descuento_promocion, "
                + "fecha_inicio_promocion, fecha_fin_promocion FROM promocion";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Promocion promocion = new Promocion();
            promocion.setIdPromocion(rs.getInt("idpromocion"));
            promocion.setDescripcion(rs.getString("descripcion_promocion"));
            promocion.setDescuento(rs.getBigDecimal("descuento_promocion"));
            promocion.setFechaInicio(rs.getDate("fecha_inicio_promocion").toLocalDate());
            promocion.setFechaFin(rs.getDate("fecha_fin_promocion").toLocalDate());
            promociones.add(promocion);
        }
        connection.close();
        return promociones;
    }
    
    public static List<Promocion> obtenerPromocionesPorBebida(int idBebida) throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        String query = "SELECT p.idpromocion, p.descripcion_promocion, p.descuento_promocion, " +
                       "p.fecha_inicio_promocion, p.fecha_fin_promocion " +
                       "FROM promocion p " +
                       "JOIN promocion_bebida pb ON p.idpromocion = pb.promocion_idpromocion " +
                       "WHERE pb.bebida_idbebida = ?";

        try (Connection connection = Conexion.abrirConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idBebida);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promocion promocion = new Promocion();
                    promocion.setIdPromocion(rs.getInt("idpromocion"));
                    promocion.setDescripcion(rs.getString("descripcion_promocion"));
                    promocion.setDescuento(rs.getBigDecimal("descuento_promocion"));
                    promocion.setFechaInicio(rs.getDate("fecha_inicio_promocion").toLocalDate());
                    promocion.setFechaFin(rs.getDate("fecha_fin_promocion").toLocalDate());

                    promociones.add(promocion);
                }
            }
        }
        return promociones;
    }
}

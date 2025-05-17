package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author eugen
 */
public class ProveedorDAO {
    public boolean create(Proveedor proveedor) throws SQLException{
        if(proveedor == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "INSERT INTO proveedor (nombre_proveedor, direccion_proveedor, telefono_proveedor, "
                + "correo_proveedor) VALUES (?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, proveedor.getNombre());
        ps.setString(2, proveedor.getDireccion());
        ps.setString(3, proveedor.getTelefono());
        ps.setString(4, proveedor.getCorreo());
        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) proveedor.setIdProveedor(generatedKeys.getInt(1));
            connection.close();
            return true;
        }else{
            connection.close();
            return false;
        }
    }
    
    public static Proveedor read(int id)throws SQLException{
        Proveedor proveedor = new Proveedor();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idproveedor, nombre_proveedor, direccion_proveedor, telefono_proveedor, "
                + "correo_proveedor FROM proveedor WHERE idproveedor = ?";
        PreparedStatement ps = connection.prepareCall(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            proveedor.setIdProveedor(rs.getInt("idproveedor"));
            proveedor.setNombre(rs.getString("nombre_proveedor"));
            proveedor.setDireccion(rs.getString("direccion_proveedor"));
            proveedor.setTelefono(rs.getString("telefono_proveedor"));
            proveedor.setCorreo(rs.getString("correo_proveedor"));
        }
        connection.close();
        return proveedor;
    }
    
    public static boolean update(Proveedor proveedor) throws SQLException{
        if(proveedor == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "UPDATE proveedor SET nombre_proveedor = ?, direccion_proveedor = ?, "
                + "telefono_proveedor = ?, correo_proveedor = ? WHERE idproveedor = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, proveedor.getNombre());
        ps.setString(2, proveedor.getDireccion());
        ps.setString(3, proveedor.getTelefono());
        ps.setString(4, proveedor.getCorreo());
        ps.setInt(5, proveedor.getIdProveedor());
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    }
    
    public static boolean delete(int id) throws SQLException{
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "DELETE FROM proveedor WHERE idproveedor = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    } 
    
    public static ArrayList<Proveedor> readAll() throws SQLException{
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idproveedor, nombre_proveedor, direccion_proveedor, telefono_proveedor, "
                + "correo_proveedor FROM proveedor";
        PreparedStatement ps = connection.prepareCall(query);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(rs.getInt("idproveedor"));
            proveedor.setNombre(rs.getString("nombre_proveedor"));
            proveedor.setDireccion(rs.getString("direccion_proveedor"));
            proveedor.setTelefono(rs.getString("telefono_proveedor"));
            proveedor.setCorreo(rs.getString("correo_proveedor"));
            proveedores.add(proveedor);
        }
        connection.close();
        return proveedores;
    }     
}

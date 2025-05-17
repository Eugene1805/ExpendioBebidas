package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author eugen
 */
public class ClienteDAO {
    public boolean create(Cliente cliente) throws SQLException{
        if(cliente == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "INSERT INTO cliente (razon_social_cliente, direccion_cliente, telefono_cliente, "
                + "tipo_cliente, rfc_cliente) VALUES (?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, cliente.getRazonSocialCliente());
        ps.setString(2, cliente.getDireccionCliente());
        ps.setString(3, cliente.getTelefonoCliente());
        ps.setString(4, cliente.getTipo());
        ps.setString(5, cliente.getRfc());
        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) cliente.setIdCliente(generatedKeys.getInt(1));
            connection.close();
            return true;
        }else{
            connection.close();
            return false;
        }
    }
    
    public static Cliente read(int id)throws SQLException{
        Cliente cliente = new Cliente();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idcliente, razon_social_cliente, direccion_cliente, telefono_cliente, "
                + "tipo_cliente, rfc_cliente FROM cliente WHERE idcliente = ?";
        PreparedStatement ps = connection.prepareCall(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            cliente.setIdCliente(rs.getInt("idcliente"));
            cliente.setRazonSocialCliente(rs.getString("razon_social_cliente"));
            cliente.setDireccionCliente(rs.getString("direccion_cliente"));
            cliente.setTelefonoCliente(rs.getString("telefono_cliente"));
            cliente.setTipo(rs.getString("tipo_cliente"));
            cliente.setRfc(rs.getString("rfc_cliente"));
        }
        connection.close();
        return cliente;
    }
    
    public static boolean update(Cliente cliente) throws SQLException{
        if(cliente == null) return false;
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "UPDATE cliente SET razon_social_cliente = ?, direccion_cliente = ?, "
                + "telefono_cliente = ?, tipo_cliente = ?, rfc_cliente = ? WHERE idcliente = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, cliente.getRazonSocialCliente());
        ps.setString(2, cliente.getDireccionCliente());
        ps.setString(3, cliente.getTelefonoCliente());
        ps.setString(4, cliente.getTipo());
        ps.setString(5, cliente.getRfc());
        ps.setInt(5, cliente.getIdCliente());
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    }
    
    public static boolean delete(int id) throws SQLException{
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "DELETE FROM cliente WHERE idcliente = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        int affectedRows= ps.executeUpdate();
        connection.close();
        return (affectedRows > 0);
    } 
    
    public static ArrayList<Cliente> readAll() throws SQLException{
        ArrayList<Cliente> clientes = new ArrayList<>();
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String query = "SELECT idcliente, razon_social_cliente, direccion_cliente, telefono_cliente, "
                + "tipo_cliente, rfc_cliente FROM cliente";
        PreparedStatement ps = connection.prepareCall(query);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Cliente cliente = new Cliente();
            cliente.setIdCliente(rs.getInt("idcliente"));
            cliente.setRazonSocialCliente(rs.getString("razon_social_cliente"));
            cliente.setDireccionCliente(rs.getString("direccion_cliente"));
            cliente.setTelefonoCliente(rs.getString("telefono_cliente"));
            cliente.setTipo(rs.getString("tipo_cliente"));
            cliente.setRfc(rs.getString("rfc_cliente"));
            clientes.add(cliente);
        }
        connection.close();
        return clientes;
    }  
}

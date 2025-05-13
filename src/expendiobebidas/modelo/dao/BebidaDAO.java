package expendiobebidas.modelo.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.Bebida;
import java.util.ArrayList;


/**
 *
 * @author eugen
 */
public class BebidaDAO {
    public static int create(Bebida bebida) throws SQLException{
        Connection conexion = Conexion.abrirConexion();
        String query = "INSERT INTO bebida (nombre, descripcion, stock_minimo, precio) VALUES "
                + "(?,?,?,?)";
        
        PreparedStatement ps = conexion.prepareStatement(query);
        ResultSet rs= ps.getGeneratedKeys();
        if(rs != null){
            return 1;
        }else{
            return 0;
        }
    }
    
    public static Bebida read()throws SQLException{
        return null;
    }
    
    public static int update(Bebida bebida) throws SQLException{
        return 0;
    }
    
    public static int delete(int id) throws SQLException{
        return 0;
    } 
    
    public static ArrayList<Bebida> readAll() SQLException{
        return null;
    }
    
}

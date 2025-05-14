package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author eugen
 */
public class UserDAO {
    public boolean login(String username, String password) throws SQLException {
        Connection connection = Conexion.abrirConexion();
        if (connection == null) throw new SQLException();

        String sql = "SELECT username, password FROM usuario WHERE username = ? AND password = AES_ENCRYPT(?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, Conexion.getAESKEY());

        ResultSet rs = stmt.executeQuery();
        return rs.next(); 
    }
    
    
}

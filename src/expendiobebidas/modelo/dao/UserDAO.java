package expendiobebidas.modelo.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
    public boolean login(String username, String password) throws SQLException{
        Connection connection = Conexion.abrirConexion();
        if(connection == null) throw new SQLException();
        String sql = "SELECT password FROM usuario WHERE username = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password");
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
            return result.verified;
        } else {
            return false;
        }
    
}

}

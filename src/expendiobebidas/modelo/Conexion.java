package expendiobebidas.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eugen
 */
public class Conexion {
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    public static Connection abrirConexion(){
        Connection conexionBD = null;
        String[] credentials = getCredentials();                 

        try{
            Class.forName(DRIVER);
            conexionBD = DriverManager.getConnection(credentials[0],credentials[1],credentials[2]);
                    
        }catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return conexionBD;
    }
    
    private static String[] getCredentials(){
        
        String[] credentials = new String[3];
        try(FileInputStream input = new FileInputStream("config.properties");) {
            Properties properties = new Properties();
            properties.load(input);
            
            credentials[0] = properties.getProperty("DB_URL");
            credentials[1] = properties.getProperty("DB_USER");
            credentials[2] = properties.getProperty("DB_PASSWORD");
            input.close();
            
        } catch(IOException e){
            e.printStackTrace();
        }finally{
            return credentials;
        }
    }
}

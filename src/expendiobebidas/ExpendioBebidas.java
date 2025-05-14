package expendiobebidas;

import expendiobebidas.controlador.LoginController;
import expendiobebidas.modelo.dao.UserDAO;
import expendiobebidas.vista.Login;
import javax.swing.SwingUtilities;
/**
 *
 * @author eugen
 */
public class ExpendioBebidas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserDAO userDao = new UserDAO();
        Login login = new Login();
        new LoginController(login, userDao);
        login.setVisible(true);
        
    }
}

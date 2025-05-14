package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.UserDAO;
import expendiobebidas.vista.Login;
import expendiobebidas.vista.Main;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author eugen
 */
public class LoginController {
    private final Login view;
    private final UserDAO userDAO;

    public LoginController(Login view, UserDAO userDAO) {
        this.view = view;
        this.userDAO = userDAO;

        this.view.btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        try {
            String username = view.getUsername();
            String password = view.getPassword();
            
            boolean success = userDAO.login(username, password);
            
            if (success) {
                // Lanzar dashboard
                Main main = new Main ();
                main.setVisible(success);
            } else {
                JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos",
                        "Error de login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

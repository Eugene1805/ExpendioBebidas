package expendiobebidas.controlador;

import expendiobebidas.modelo.dao.BebidaDAO;
import expendiobebidas.modelo.dao.ClienteDAO;
import expendiobebidas.modelo.dao.VentaDAO;
import expendiobebidas.modelo.pojo.Bebida;
import expendiobebidas.modelo.pojo.Cliente;
import expendiobebidas.vista.Ventas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author meler
 */
public class VentasController {

    private final Ventas vista;
    private final VentaDAO modeloDAO;
    private BebidaDAO bebidaDAO;
    private List<Bebida> bebidas;
    private ClienteDAO clienteDAO;
    private List<Cliente> clientes;
    public VentasController(Ventas vista) {
        this.vista = vista;
        this.modeloDAO = new VentaDAO();
        this.bebidaDAO = new BebidaDAO();
        this.bebidas = new ArrayList<>();
        this.clienteDAO = new ClienteDAO();
        this.clientes = new ArrayList<>();
        
        configurarListeners();
        cargarBebidas();
        cargarClientes();
    }
    
    private void configurarListeners(){
        //TODO
    }
    
    private void cargarBebidas(){
        try {
            bebidas = bebidaDAO.readAll();
            cargarComboBebidas();
        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cargarClientes(){
        try {
            clientes = clienteDAO.readAll();
            cargarComboClientes();
        } catch (SQLException ex) {
            Logger.getLogger(VentasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cargarComboBebidas(){
        DefaultComboBoxModel model = (DefaultComboBoxModel) vista.getCbBebidaVenta().getModel();
        model.removeAllElements();
        for(Bebida bebida: bebidas){
            model.addElement(bebida.getNombre());
        }
    }
    
    private void cargarComboClientes(){
        DefaultComboBoxModel model = (DefaultComboBoxModel) vista.getCbClienteVenta().getModel();
        model.removeAllElements();
        for(Cliente cliente: clientes){
            model.addElement(cliente.getRazonSocialCliente());
        }
    }
}

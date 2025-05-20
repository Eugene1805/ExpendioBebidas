package expendiobebidas.controlador;

import expendiobebidas.vista.Main;

/**
 *
 * @author eugen
 */
public class MainController {
    private final Main main;

    public MainController(Main main) {
        this.main = main;
        
        this.main.getMenuBebida().addActionListener(e -> showPanelBebidas());
        this.main.getMenuCliente().addActionListener(e -> showPanelClientes());
        this.main.getMenuPedidoCliente().addActionListener(e -> showPanelPedidoCliente());
        this.main.getMenuPedidoProveedor().addActionListener(e -> showPanelPedidoProveedor());
        this.main.getMenuPromocion().addActionListener(e -> showPanelPromociones());
        this.main.getMenuProveedor().addActionListener(e -> showPanelProveedores());
        this.main.getMenuReporte().addActionListener(e -> showPanelReportes());
        this.main.getMenuPuntoVenta().addActionListener(e -> showPanelVentas());
        this.main.getMenuCompra().addActionListener(e-> showPanelCompras());
    }

    private void showPanelBebidas() {
        main.getCardLayout().show(main.getPanelPrincipal(),"Registrar Bebidas");
    }

    private void showPanelClientes() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Clientes");
    }

    private void showPanelPedidoCliente() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Pedidos para Cliente");

    }

    private void showPanelPedidoProveedor() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Pedidos para Proveedor");

    }

    private void showPanelPromociones() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Crear Promocion");

    }

    private void showPanelProveedores() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Proveedores");

    }

    private void showPanelReportes() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Consultar Reportes");

    }

    private void showPanelVentas() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Venta");
    }

    private void showPanelCompras() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Compra");
    }
    
    
}

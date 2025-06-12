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

    this.main.getMenuReporteFecha().addActionListener(e -> showPanelReporteFechas());
    this.main.getMenuReporteProducto().addActionListener(e -> showPanelReporteVentaPorProducto());
    this.main.getMenuReporteCliente().addActionListener(e -> showPanelReporteStockMinimo());
    this.main.getMenuReporteProductoMasVendido().addActionListener(e -> showPanelReporteProductoMasVendido());
    this.main.getMenuReporteProductoMenosVendido().addActionListener(e -> showPanelReporteProductoMenosVendido());
    this.main.getMenuReporteProductoNoVendidoCliente().addActionListener(e -> showPanelReporteClienteNoVendido());
    this.main.getMenuReporteProductoMasVendidoCliente().addActionListener(e -> showPanelReporteClienteMasVendido());

    this.main.getMenuPuntoVenta().addActionListener(e -> showPanelVentas());
    this.main.getMenuCompra().addActionListener(e -> showPanelCompras());
}

    
    
    
    public void deshabilitarOpciones(String username){
        if(!"administrador".equals(username)){
            main.getMenuReporteFecha().setVisible(false);
            main.getMenuPromocion().setVisible(false);
            main.getMenuReporteProducto().setVisible(false);
            main.getMenuReporteCliente().setVisible(false);
        }
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
        main.getCardLayout().show(main.getPanelPrincipal(), "Registar Pedidos para Proveedor");

    }

    private void showPanelPromociones() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Crear Promocion");

    }

    private void showPanelProveedores() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Proveedores");

    }

    private void showPanelReporteFechas() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Ventas por fecha");
    }
    private void showPanelReporteVentaPorProducto() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Ventas Organizadas por Producto");
    }
    
    private void showPanelReporteStockMinimo() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Productos con Stock Minimo");
    }
    
    private void showPanelReporteProductoMasVendido() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Reporte Producto Más Vendido");
    }
    private void showPanelReporteProductoMenosVendido() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Reporte Producto Menos Vendido");
    }
    
    private void showPanelReporteClienteNoVendido() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Reporte Producto No Vendido Por Cliente");
    }
    
    private void showPanelReporteClienteMasVendido() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Reporte Producto Más Vendido a un Cliente");
    }

    private void showPanelVentas() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Venta");
    }

    private void showPanelCompras() {
        main.getCardLayout().show(main.getPanelPrincipal(), "Registrar Compra");
    }
    
    
}

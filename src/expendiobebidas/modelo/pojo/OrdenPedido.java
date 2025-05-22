/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

/**
 *
 * @author Nash
 */
public class OrdenPedido {
    private Bebida bebida;
    private PedidoCliente pedidoCliente;
    private int cantidad;
    private Double subtotal;

    public OrdenPedido() {
    }

    public OrdenPedido(Bebida bebida, PedidoCliente pedidoCliente, int cantidad, Double subtotal) {
        this.bebida = bebida;
        this.pedidoCliente = pedidoCliente;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public PedidoCliente getPedidoCliente() {
        return pedidoCliente;
    }

    public void setPedidoCliente(PedidoCliente pedidoCliente) {
        this.pedidoCliente = pedidoCliente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
    
    
    
}

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
    private int cantidadOrdenPedido;
    private Double subtotalOrdenPedido;

    public OrdenPedido() {
    }

    public OrdenPedido(Bebida bebida, PedidoCliente pedidoCliente, int cantidadOrdenPedido, Double subtotalOrdenPedido) {
        this.bebida = bebida;
        this.pedidoCliente = pedidoCliente;
        this.cantidadOrdenPedido = cantidadOrdenPedido;
        this.subtotalOrdenPedido = subtotalOrdenPedido;
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

    public int getCantidadOrdenPedido() {
        return cantidadOrdenPedido;
    }

    public void setCantidadOrdenPedido(int cantidadOrdenPedido) {
        this.cantidadOrdenPedido = cantidadOrdenPedido;
    }

    public Double getSubtotalOrdenPedido() {
        return subtotalOrdenPedido;
    }

    public void setSubtotalOrdenPedido(Double subtotalOrdenPedido) {
        this.subtotalOrdenPedido = subtotalOrdenPedido;
    }   
}

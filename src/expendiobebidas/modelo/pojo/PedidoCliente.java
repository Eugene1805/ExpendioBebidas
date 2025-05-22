/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

/**
 *
 * @author Nash
 */
public class PedidoCliente {
    private int idPedidoCliente;
    private String fecha;
    private Float descuento;
    private Float total;
    private Cliente cliente;

    public PedidoCliente() {
    }

    public PedidoCliente(int idPedidoCliente, String fecha, Float descuento, Float total, Cliente cliente) {
        this.idPedidoCliente = idPedidoCliente;
        this.fecha = fecha;
        this.descuento = descuento;
        this.total = total;
        this.cliente = cliente;
    }

    public int getIdPedidoCliente() {
        return idPedidoCliente;
    }

    public void setIdPedidoCliente(int idPedidoCliente) {
        this.idPedidoCliente = idPedidoCliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Float getDescuento() {
        return descuento;
    }

    public void setDescuento(Float descuento) {
        this.descuento = descuento;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
}

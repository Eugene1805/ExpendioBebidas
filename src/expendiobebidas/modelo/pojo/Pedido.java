/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Nash
 */
public class Pedido {
    private int idPedido;
    private String estadoPedido;
    private LocalDate fechaPedido;
    private Proveedor proveedor;
    private Double total;

    public Pedido() {
    }

    public Pedido(int idPedido, String estadoPedido, LocalDate fechaPedido, Proveedor proveedor, Double total) {
        this.idPedido = idPedido;
        this.estadoPedido = estadoPedido;
        this.fechaPedido = fechaPedido;
        this.proveedor = proveedor;
        this.total = total;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    
    
}

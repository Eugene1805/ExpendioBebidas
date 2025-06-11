/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Nash
 */
public class PedidoCliente {
    private int idPedidoCliente;
    private LocalDate fecha;
    private BigDecimal descuentoPedidoCliente;
    private BigDecimal totalPedidoCliente;
    private Cliente cliente;

    public PedidoCliente() {
    }

    public PedidoCliente(int idPedidoCliente, LocalDate fecha, BigDecimal descuentoPedidoCliente, BigDecimal totalPedidoCliente, Cliente cliente) {
        this.idPedidoCliente = idPedidoCliente;
        this.fecha = fecha;
        this.descuentoPedidoCliente = descuentoPedidoCliente;
        this.totalPedidoCliente = totalPedidoCliente;
        this.cliente = cliente;
    }

    public int getIdPedidoCliente() {
        return idPedidoCliente;
    }

    public void setIdPedidoCliente(int idPedidoCliente) {
        this.idPedidoCliente = idPedidoCliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getDescuentoPedidoCliente() {
        return descuentoPedidoCliente;
    }

    public void setDescuentoPedidoCliente(BigDecimal descuentoPedidoCliente) {
        this.descuentoPedidoCliente = descuentoPedidoCliente;
    }

    public BigDecimal getTotalPedidoCliente() {
        return totalPedidoCliente;
    }

    public void setTotalPedidoCliente(BigDecimal totalPedidoCliente) {
        this.totalPedidoCliente = totalPedidoCliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    
    
}

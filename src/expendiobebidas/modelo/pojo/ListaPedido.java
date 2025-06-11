/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

import java.time.LocalDate;


public class ListaPedido {
    private int idPedido;
    private int cantidad;
    private LocalDate fechaPedido;
    private String estado;
    private Bebida bebida;

    public ListaPedido() {
    }

    public ListaPedido(int idPedido, int cantidad, LocalDate fechaPedido, String estado, Bebida bebida) {
        this.idPedido = idPedido;
        this.cantidad = cantidad;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.bebida = bebida;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }
    
    
}

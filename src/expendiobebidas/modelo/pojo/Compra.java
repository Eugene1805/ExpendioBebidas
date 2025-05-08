/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiobebidas.modelo.pojo;

import java.time.LocalDateTime;

/**
 *
 * @author Nash
 */
public class Compra {
    private String folio;
    private LocalDateTime fechaCompra;
    private Float totalCompra;

    public Compra() {
    }

    public Compra(String folio, LocalDateTime fechaCompra, Float totalCompra) {
        this.folio = folio;
        this.fechaCompra = fechaCompra;
        this.totalCompra = totalCompra;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Float getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(Float totalCompra) {
        this.totalCompra = totalCompra;
    }   
}

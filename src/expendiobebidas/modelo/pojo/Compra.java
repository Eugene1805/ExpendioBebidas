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
    private LocalDateTime fecha;
    private Float decimal;

    public Compra() {
    }

    public Compra(String folio, LocalDateTime fecha, Float decimal) {
        this.folio = folio;
        this.fecha = fecha;
        this.decimal = decimal;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Float getDecimal() {
        return decimal;
    }

    public void setDecimal(Float decimal) {
        this.decimal = decimal;
    }
    
    
}

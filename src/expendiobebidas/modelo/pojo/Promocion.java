package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Nash
 */
public class Promocion {
    private int idPromocion;
    private String descripcion;
    private BigDecimal descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Promocion() {
    }

    public Promocion(int idPromocion, String descripcion, BigDecimal descuento, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idPromocion = idPromocion;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    @Override
    public String toString(){
        return descripcion;
    }
}

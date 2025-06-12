package expendiobebidas.modelo.pojo;

import java.time.LocalDate;

/**
 *
 * @author meler
 */
public class ReporteVentaPorFecha {
    private String folio;
    private LocalDate fecha;
    private double descuento;
    private String cliente;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}

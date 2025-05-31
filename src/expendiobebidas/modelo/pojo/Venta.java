package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;


/**
 *
 * @author Nash
 */
public class Venta {
    private int idVenta;
    private String fecha;
    private BigDecimal descuento;
    private BigDecimal total;
    private Cliente cliente;
    private String folio;

    public Venta() {
    }

    public Venta(int idVenta, String fecha, BigDecimal descuento, BigDecimal total, Cliente cliente, String folio) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.descuento = descuento;
        this.total = total;
        this.cliente = cliente;
        this.folio = folio;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }
    
}

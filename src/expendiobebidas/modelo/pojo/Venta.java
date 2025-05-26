package expendiobebidas.modelo.pojo;


/**
 *
 * @author Nash
 */
public class Venta {
    private int idVenta;
    private String fecha;
    private Float descuento;
    private Float total;
    private Cliente cliente;
    private String folio;

    public Venta() {
    }

    public Venta(int idVenta, String fecha, Float descuento, Float total, Cliente cliente, String folio) {
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

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }
    
}

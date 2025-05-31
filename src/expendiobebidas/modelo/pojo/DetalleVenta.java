package expendiobebidas.modelo.pojo;

/**
 *
 * @author Nash
 */
public class DetalleVenta {
    private Venta venta;
    private Bebida bebida;
    private Double subtotal;
    private int cantidad;
    private Float precioMomento;

    public DetalleVenta() {
    }

    public DetalleVenta(Venta venta, Bebida bebida, Double subtotal, int cantidad, Float precioMomento) {
        this.venta = venta;
        this.bebida = bebida;
        this.subtotal = subtotal;
        this.cantidad = cantidad;
        this.precioMomento = precioMomento;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecioMomento() {
        return precioMomento;
    }

    public void setPrecioMomento(Float precioMomento) {
        this.precioMomento = precioMomento;
    }
    
    
}

package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;

/**
 *
 * @author Nash
 */
public class DetalleVenta {
    private Venta venta;
    private Bebida bebida;
    private Double subtotal;
    private int cantidad;
    private BigDecimal precioMomento;
    private Promocion promocionAplicada;

    public DetalleVenta() {
    }

    public DetalleVenta(Venta venta, Bebida bebida, Double subtotal, int cantidad, BigDecimal precioMomento) {
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

    public BigDecimal getPrecioMomento() {
        return precioMomento;
    }

    public void setPrecioMomento(BigDecimal precioMomento) {
        this.precioMomento = precioMomento;
    }
    
    public Promocion getPromocionAplicada() {
        return promocionAplicada;
    }

    public void setPromocionAplicada(Promocion promocionAplicada) {
        this.promocionAplicada = promocionAplicada;
    }
}

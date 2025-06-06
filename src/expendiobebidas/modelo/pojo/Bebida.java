package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;

/**
 *
 * @author eugen
 */
public class Bebida {
    private int idBebida;
    private String nombre;
    private String descripcion;
    private int stockMinimo;
    private BigDecimal precio;
    private int stockActual;

    public Bebida() {
    }

    public Bebida(int idBebida, String nombre, String descripcion, int stockMinimo, BigDecimal precio, int stockActual) {
        this.idBebida = idBebida;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
        this.stockActual = stockActual;
    }

    public int getIdBebida() {
        return idBebida;
    }

    public void setIdBebida(int id) {
        this.idBebida = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
    
    @Override
    public String toString(){
        return nombre;
    }
}

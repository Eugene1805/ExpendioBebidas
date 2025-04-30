package expendiobebidas.modelo.pojo;

/**
 *
 * @author eugen
 */
public class Bebida {
    private int id;
    private String nombre;
    private String descripcion;
    private int stockMinimo;
    private float precio;
    private int stockActual;

    public Bebida() {
    }

    public Bebida(int id, String nombre, String descripcion, int stockMinimo, float precio, int stockActual) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
        this.stockActual = stockActual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
    
    
}

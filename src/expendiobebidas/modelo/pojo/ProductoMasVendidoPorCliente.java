package expendiobebidas.modelo.pojo;

/**
 *
 * @author meler
 */

public class ProductoMasVendidoPorCliente {
    private String bebida;
    private int totalVendidos;

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public int getTotalVendidos() {
        return totalVendidos;
    }

    public void setTotalVendidos(int totalVendidos) {
        this.totalVendidos = totalVendidos;
    }
}

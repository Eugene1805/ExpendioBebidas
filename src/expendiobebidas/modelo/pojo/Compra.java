package expendiobebidas.modelo.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Nash
 */
public class Compra {
    private String folio;
    private LocalDate fechaCompra;
    private BigDecimal totalCompra;

    public Compra() {
    }

    public Compra(String folio, LocalDate fechaCompra, BigDecimal totalCompra) {
        this.folio = folio;
        this.fechaCompra = fechaCompra;
        this.totalCompra = totalCompra;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
        this.totalCompra = totalCompra;
    }   
}

package expendiobebidas.modelo.pojo;

/**
 *
 * @author Nash
 */
public class PromocionBebida {
    private Promocion promocion;
    private Bebida bebida;

    public PromocionBebida() {
    }

    public PromocionBebida(Promocion promocion, Bebida bebida) {
        this.promocion = promocion;
        this.bebida = bebida;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }
    
    
}

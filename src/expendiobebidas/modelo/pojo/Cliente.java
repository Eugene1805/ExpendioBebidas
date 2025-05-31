package expendiobebidas.modelo.pojo;

/**
 *
 * @author Nash
 */
public class Cliente {
    private int idCliente;
    private String razonSocialCliente;
    private String direccionCliente;
    private String telefonoCliente;
    private String tipo;
    private String rfc;

    public Cliente() {
    }

    public Cliente(int idCliente, String razonSocialCliente, String direccionCliente, String telefonoCliente, String tipo, String rfc) {
        this.idCliente = idCliente;
        this.razonSocialCliente = razonSocialCliente;
        this.direccionCliente = direccionCliente;
        this.telefonoCliente = telefonoCliente;
        this.tipo = tipo;
        this.rfc = rfc;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getRazonSocialCliente() {
        return razonSocialCliente;
    }

    public void setRazonSocialCliente(String razonSocialCliente) {
        this.razonSocialCliente = razonSocialCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    } 
    
    @Override
    public String toString(){
        return razonSocialCliente;
    }
}

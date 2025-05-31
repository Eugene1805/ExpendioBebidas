package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.pojo.Venta;

/**
 *
 * @author eugen
 */
public class VentaDAO {
    //TODO poder crear registros de la tabla venta y actualizar las tablas relacionadas
    // se actualizara el stock actual y se ejecutaran los triggers ya definidos
    public void insertar(Venta venta){
        //TODO
    }
    
    public int obtenerUltimoFolio(int anioActual){
        String query = "SELECT COALESCE(MAX(CAST(SUBSTRING(folio_venta, 10) AS UNSIGNED)), 0) " +
                        "FROM venta WHERE folio_venta LIKE CONCAT('VENT-', ? , '-%')";
        //TODO
        return -1;
    }
}

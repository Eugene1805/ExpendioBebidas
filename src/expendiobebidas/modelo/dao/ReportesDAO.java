package expendiobebidas.modelo.dao;

import expendiobebidas.modelo.Conexion;
import expendiobebidas.modelo.pojo.ReporteVentaPorFecha;
import expendiobebidas.modelo.pojo.ReporteVentaPorProducto;
import expendiobebidas.modelo.pojo.ReporteStockMinimo;
import expendiobebidas.modelo.pojo.ProductoMasVendido;
import expendiobebidas.modelo.pojo.ProductoMenosVendido;
import expendiobebidas.modelo.pojo.ProductoNoVendido;
import expendiobebidas.modelo.pojo.ProductoMasVendidoPorCliente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author meler
 */
public class ReportesDAO {

    public static List<ReporteVentaPorFecha> obtenerVentasEntreFechas(LocalDate inicio, LocalDate fin) throws SQLException {
        List<ReporteVentaPorFecha> lista = new ArrayList<>();

        String query = "SELECT v.folio_venta, v.fecha_venta, v.descuento_venta, c.razon_social_cliente " +
                       "FROM venta v " +
                       "JOIN cliente c ON v.cliente_idcliente = c.idcliente " +
                       "WHERE v.fecha_venta BETWEEN ? AND ?";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReporteVentaPorFecha r = new ReporteVentaPorFecha();
                r.setFolio(rs.getString("folio_venta"));
                r.setFecha(rs.getDate("fecha_venta").toLocalDate());
                r.setDescuento(rs.getDouble("descuento_venta"));
                r.setCliente(rs.getString("razon_social_cliente"));
                lista.add(r);
            }
        }

        return lista;
    }
    
    public static List<ReporteVentaPorProducto> obtenerVentasPorProducto() throws SQLException {
    List<ReporteVentaPorProducto> lista = new ArrayList<>();

    String query = "SELECT b.nombre AS bebida, v.folio_venta, dv.cantidad_detalle_venta, dv.subtotal_detalle_venta " +
                   "FROM detalle_venta dv " +
                   "JOIN bebida b ON dv.bebida_idbebida = b.idbebida " +
                   "JOIN venta v ON dv.venta_idventa = v.idventa";

    try (Connection conn = Conexion.abrirConexion();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            ReporteVentaPorProducto reporte = new ReporteVentaPorProducto();
            reporte.setBebida(rs.getString("bebida"));
            reporte.setFolioVenta(rs.getString("folio_venta"));
            reporte.setCantidad(rs.getInt("cantidad_detalle_venta"));
            reporte.setSubtotal(rs.getDouble("subtotal_detalle_venta"));
            lista.add(reporte);
        }
    }

    return lista;
}

    public static List<ReporteStockMinimo> obtenerStockMinimo() throws SQLException {
        List<ReporteStockMinimo> lista = new ArrayList<>();

        String query = "SELECT nombre, stock_actual, stock_minimo FROM bebida WHERE stock_actual <= stock_minimo";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReporteStockMinimo reporte = new ReporteStockMinimo();
                reporte.setNombre(rs.getString("nombre"));
                reporte.setStockActual(rs.getInt("stock_actual"));
                reporte.setStockMinimo(rs.getInt("stock_minimo"));
                lista.add(reporte);
            }
        }

        return lista;
    }
    
    public static List<ProductoMasVendido> obtenerProductosMasVendidos() throws SQLException {
        List<ProductoMasVendido> lista = new ArrayList<>();

        String query = "SELECT nombre, total_vendido FROM reporte_mas_vendidos";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductoMasVendido producto = new ProductoMasVendido();
                producto.setNombre(rs.getString("nombre"));
                producto.setTotalVendido(rs.getBigDecimal("total_vendido"));
                lista.add(producto);
            }
        }

        return lista;
    }
    
    public static List<ProductoMenosVendido> obtenerProductosMenosVendidos() throws SQLException {
        List<ProductoMenosVendido> lista = new ArrayList<>();

        String query = "SELECT b.nombre AS producto, SUM(dv.cantidad_detalle_venta) AS total_vendidos " +
                       "FROM detalle_venta dv " +
                       "JOIN bebida b ON dv.bebida_idbebida = b.idbebida " +
                       "GROUP BY b.idbebida, b.nombre " +
                       "ORDER BY total_vendidos ASC " +
                       "LIMIT 3";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductoMenosVendido producto = new ProductoMenosVendido();
                producto.setProducto(rs.getString("producto"));
                producto.setTotalVendidos(rs.getInt("total_vendidos"));
                lista.add(producto);
            }
        }

        return lista;
    }
    
    public static List<ProductoNoVendido> obtenerProductosNoVendidosPorCliente(int idCliente) throws SQLException {
        List<ProductoNoVendido> lista = new ArrayList<>();

        String query = "SELECT b.nombre " +
                       "FROM bebida b " +
                       "WHERE NOT EXISTS ( " +
                       "  SELECT 1 FROM detalle_venta dv " +
                       "  JOIN venta v ON dv.venta_idventa = v.idventa " +
                       "  WHERE dv.bebida_idbebida = b.idbebida " +
                       "  AND v.cliente_idcliente = ? " +
                       ")";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductoNoVendido producto = new ProductoNoVendido();
                producto.setNombre(rs.getString("nombre"));
                lista.add(producto);
            }
        }

        return lista;
    }
    
    public static List<ProductoMasVendidoPorCliente> obtenerProductoMasVendidoPorCliente(int idCliente) throws SQLException {
        List<ProductoMasVendidoPorCliente> lista = new ArrayList<>();

        String query = "SELECT b.nombre, SUM(dv.cantidad_detalle_venta) AS total_vendidos " +
                       "FROM detalle_venta dv " +
                       "JOIN venta v ON dv.venta_idventa = v.idventa " +
                       "JOIN bebida b ON dv.bebida_idbebida = b.idbebida " +
                       "WHERE v.cliente_idcliente = ? " +
                       "GROUP BY b.idbebida, b.nombre " +
                       "ORDER BY total_vendidos DESC " +
                       "LIMIT 1";

        try (Connection conn = Conexion.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductoMasVendidoPorCliente producto = new ProductoMasVendidoPorCliente();
                producto.setBebida(rs.getString("nombre"));
                producto.setTotalVendidos(rs.getInt("total_vendidos"));
                lista.add(producto);
            }
        }

        return lista;
    }
}
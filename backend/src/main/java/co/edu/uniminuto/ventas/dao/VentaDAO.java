package co.edu.uniminuto.ventas.dao;

import co.edu.uniminuto.ventas.conexion.Conexion;
import co.edu.uniminuto.ventas.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla ventas. */
public class VentaDAO {

    /** Inserta la venta dentro de la transacción recibida y devuelve el id generado. */
    public int insertar(Connection con, Venta v) throws SQLException {
        String sql = "INSERT INTO ventas (id_producto, cantidad, precio_venta) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdProducto());
            ps.setInt(2, v.getCantidad());
            ps.setBigDecimal(3, v.getPrecioVenta());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<Venta> listar() throws SQLException {
        String sql = """
                SELECT v.id_venta, DATE_FORMAT(v.fecha, '%Y-%m-%d %H:%i:%s') AS fecha,
                       v.id_producto, p.nombre, v.cantidad, v.precio_venta,
                       v.cantidad * v.precio_venta AS total
                FROM ventas v
                JOIN productos p ON p.id_producto = v.id_producto
                ORDER BY v.id_venta DESC
                """;
        List<Venta> ventas = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFecha(rs.getString("fecha"));
                v.setIdProducto(rs.getInt("id_producto"));
                v.setNombreProducto(rs.getString("nombre"));
                v.setCantidad(rs.getInt("cantidad"));
                v.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                v.setTotal(rs.getBigDecimal("total"));
                ventas.add(v);
            }
        }
        return ventas;
    }
}

package co.edu.uniminuto.ventas.dao;

import co.edu.uniminuto.ventas.conexion.Conexion;
import co.edu.uniminuto.ventas.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de la tabla productos (CRUD del inventario).
 * Los métodos que reciben un Connection participan en una transacción abierta
 * por la capa de servicio; los demás abren y cierran su propia conexión.
 */
public class ProductoDAO {

    private static final String SELECT_BASE =
            "SELECT id_producto, nombre, descripcion, precio, cantidad FROM productos";

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + " ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }
        return productos;
    }

    public Optional<Producto> buscarPorId(int idProducto) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE id_producto = ?")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Producto insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getCantidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setIdProducto(keys.getInt(1));
                }
            }
        }
        return p;
    }

    public boolean actualizar(Producto p) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, cantidad = ? WHERE id_producto = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getCantidad());
            ps.setInt(5, p.getIdProducto());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int idProducto) throws SQLException {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM productos WHERE id_producto = ?")) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() == 1;
        }
    }

    // ----------------- Métodos usados dentro de una transacción -----------------

    /** Lee el producto y bloquea su fila (SELECT ... FOR UPDATE) hasta el COMMIT o ROLLBACK. */
    public Optional<Producto> bloquearParaVenta(Connection con, int idProducto) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE id_producto = ? FOR UPDATE")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void descontarStock(Connection con, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE productos SET cantidad = cantidad - ? WHERE id_producto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("precio"),
                rs.getInt("cantidad"));
    }
}

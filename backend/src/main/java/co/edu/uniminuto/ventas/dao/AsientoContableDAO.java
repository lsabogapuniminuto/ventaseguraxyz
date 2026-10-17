package co.edu.uniminuto.ventas.dao;

import co.edu.uniminuto.ventas.conexion.Conexion;
import co.edu.uniminuto.ventas.modelo.AsientoContable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla asientos_contables, compartida con el sistema de contabilidad. */
public class AsientoContableDAO {

    public void insertar(Connection con, AsientoContable a) throws SQLException {
        String sql = """
                INSERT INTO asientos_contables (id_venta, cuenta_debito, cuenta_credito, valor, descripcion)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getIdVenta());
            ps.setString(2, a.getCuentaDebito());
            ps.setString(3, a.getCuentaCredito());
            ps.setBigDecimal(4, a.getValor());
            ps.setString(5, a.getDescripcion());
            ps.executeUpdate();
        }
    }

    public List<AsientoContable> listar() throws SQLException {
        String sql = """
                SELECT id_asiento, id_venta, DATE_FORMAT(fecha, '%Y-%m-%d %H:%i:%s') AS fecha,
                       cuenta_debito, cuenta_credito, valor, descripcion
                FROM asientos_contables
                ORDER BY id_asiento DESC
                """;
        List<AsientoContable> asientos = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AsientoContable a = new AsientoContable();
                a.setIdAsiento(rs.getInt("id_asiento"));
                a.setIdVenta(rs.getInt("id_venta"));
                a.setFecha(rs.getString("fecha"));
                a.setCuentaDebito(rs.getString("cuenta_debito"));
                a.setCuentaCredito(rs.getString("cuenta_credito"));
                a.setValor(rs.getBigDecimal("valor"));
                a.setDescripcion(rs.getString("descripcion"));
                asientos.add(a);
            }
        }
        return asientos;
    }
}

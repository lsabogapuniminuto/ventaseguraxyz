package co.edu.uniminuto.ventas.servicio;

import co.edu.uniminuto.ventas.conexion.Conexion;
import co.edu.uniminuto.ventas.dao.ProductoDAO;
import co.edu.uniminuto.ventas.dao.VentaDAO;
import co.edu.uniminuto.ventas.modelo.Producto;
import co.edu.uniminuto.ventas.modelo.Venta;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Registra una venta como UNA transacción:
 *   1. bloquea el producto y valida el stock
 *   2. inserta la venta
 *   3. descuenta el inventario
 *   4. genera el asiento contable (integración con contabilidad)
 * Si todo sale bien se ejecuta COMMIT; ante cualquier error, ROLLBACK.
 */
public class VentaService {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final IntegracionContable integracionContable = new IntegracionContable();

    /**
     * @param simularFalloContable si es true, la contabilidad falla después de guardar la venta
     *                             y descontar el stock, para demostrar el ROLLBACK.
     */
    public Venta registrarVenta(Venta venta, boolean simularFalloContable) throws SQLException {
        validarEntrada(venta);

        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);                                   // INICIO DE LA TRANSACCIÓN
            System.out.println("[TX] START TRANSACTION - venta de producto " + venta.getIdProducto());
            try {
                Producto producto = productoDAO.bloquearParaVenta(con, venta.getIdProducto())
                        .orElseThrow(() -> new VentaException(404, "El producto no existe"));

                if (producto.getCantidad() < venta.getCantidad()) {
                    throw new VentaException(409, "Stock insuficiente: hay " + producto.getCantidad()
                            + " unidades de " + producto.getNombre());
                }
                if (venta.getPrecioVenta() == null) {
                    venta.setPrecioVenta(producto.getPrecio());
                }

                int idVenta = ventaDAO.insertar(con, venta);
                System.out.println("[TX]   INSERT venta #" + idVenta);

                productoDAO.descontarStock(con, venta.getIdProducto(), venta.getCantidad());
                System.out.println("[TX]   UPDATE stock -" + venta.getCantidad());

                venta.setIdVenta(idVenta);
                venta.setNombreProducto(producto.getNombre());
                integracionContable.registrarAsiento(con, venta, simularFalloContable);
                System.out.println("[TX]   INSERT asiento contable de la venta #" + idVenta);

                con.commit();                                           // CONFIRMAR
                System.out.println("[TX] COMMIT - venta #" + idVenta + " confirmada");
                return venta;
            } catch (SQLException | RuntimeException e) {
                con.rollback();                                         // DESHACER
                System.out.println("[TX] ROLLBACK - " + e.getMessage());
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private void validarEntrada(Venta venta) {
        if (venta == null || venta.getIdProducto() <= 0) {
            throw new VentaException(400, "Debe seleccionar un producto");
        }
        if (venta.getCantidad() <= 0) {
            throw new VentaException(400, "La cantidad debe ser mayor que cero");
        }
        if (venta.getPrecioVenta() != null && venta.getPrecioVenta().signum() < 0) {
            throw new VentaException(400, "El precio de venta no puede ser negativo");
        }
    }
}

package co.edu.uniminuto.ventas.servicio;

import co.edu.uniminuto.ventas.dao.AsientoContableDAO;
import co.edu.uniminuto.ventas.modelo.AsientoContable;
import co.edu.uniminuto.ventas.modelo.Venta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Integra el sistema de ventas con el sistema de contabilidad: cada venta genera
 * un asiento de partida doble (débito Caja, crédito Ingresos) en la misma transacción.
 */
public class IntegracionContable {

    private final AsientoContableDAO asientoDAO = new AsientoContableDAO();

    public void registrarAsiento(Connection con, Venta venta, boolean simularFallo) throws SQLException {
        if (simularFallo) {
            throw new VentaException(503, "El sistema de contabilidad no respondió (fallo simulado)");
        }
        BigDecimal total = venta.getPrecioVenta().multiply(BigDecimal.valueOf(venta.getCantidad()));

        AsientoContable asiento = new AsientoContable();
        asiento.setIdVenta(venta.getIdVenta());
        asiento.setCuentaDebito(AsientoContable.CUENTA_CAJA);
        asiento.setCuentaCredito(AsientoContable.CUENTA_INGRESOS);
        asiento.setValor(total);
        asiento.setDescripcion("Venta #" + venta.getIdVenta() + " - " + venta.getCantidad()
                + " x " + venta.getNombreProducto());
        asientoDAO.insertar(con, asiento);
    }
}

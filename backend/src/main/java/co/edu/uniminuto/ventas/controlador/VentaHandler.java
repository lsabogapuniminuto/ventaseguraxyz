package co.edu.uniminuto.ventas.controlador;

import co.edu.uniminuto.ventas.dao.VentaDAO;
import co.edu.uniminuto.ventas.modelo.Venta;
import co.edu.uniminuto.ventas.servicio.VentaService;
import com.sun.net.httpserver.HttpExchange;

/**
 * GET  /api/ventas                        historial de ventas
 * POST /api/ventas                        registra una venta (transacción)
 * POST /api/ventas?simularErrorContable=true  fuerza un fallo para demostrar el ROLLBACK
 */
public class VentaHandler extends BaseHandler {

    public static final String RUTA = "/api/ventas";
    private final VentaService servicio = new VentaService();
    private final VentaDAO dao = new VentaDAO();

    @Override
    protected void atender(HttpExchange ex) throws Exception {
        switch (ex.getRequestMethod()) {
            case "GET" -> responder(ex, 200, dao.listar());
            case "POST" -> {
                Venta venta = leerJson(ex, Venta.class);
                responder(ex, 201, servicio.registrarVenta(venta, parametro(ex, "simularErrorContable")));
            }
            default -> responderError(ex, 405, "Método no permitido");
        }
    }
}

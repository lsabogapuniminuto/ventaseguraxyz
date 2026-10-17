package co.edu.uniminuto.ventas.controlador;

import co.edu.uniminuto.ventas.dao.AsientoContableDAO;
import com.sun.net.httpserver.HttpExchange;

/** GET /api/contabilidad/asientos: endpoint que consume el sistema de contabilidad. */
public class ContabilidadHandler extends BaseHandler {

    public static final String RUTA = "/api/contabilidad/asientos";
    private final AsientoContableDAO dao = new AsientoContableDAO();

    @Override
    protected void atender(HttpExchange ex) throws Exception {
        if ("GET".equals(ex.getRequestMethod())) {
            responder(ex, 200, dao.listar());
        } else {
            responderError(ex, 405, "Método no permitido");
        }
    }
}

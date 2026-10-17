package co.edu.uniminuto.ventas.controlador;

import co.edu.uniminuto.ventas.dao.ProductoDAO;
import co.edu.uniminuto.ventas.modelo.Producto;
import co.edu.uniminuto.ventas.servicio.VentaException;
import com.sun.net.httpserver.HttpExchange;

/**
 * GET    /api/productos        lista el inventario
 * GET    /api/productos/{id}   consulta un producto
 * POST   /api/productos        crea un producto
 * PUT    /api/productos/{id}   actualiza un producto (incluye el stock)
 * DELETE /api/productos/{id}   elimina un producto
 */
public class ProductoHandler extends BaseHandler {

    public static final String RUTA = "/api/productos";
    private final ProductoDAO dao = new ProductoDAO();

    @Override
    protected void atender(HttpExchange ex) throws Exception {
        Integer id = idDeRuta(ex, RUTA);
        switch (ex.getRequestMethod()) {
            case "GET" -> {
                if (id == null) {
                    responder(ex, 200, dao.listar());
                } else {
                    responder(ex, 200, dao.buscarPorId(id)
                            .orElseThrow(() -> new VentaException(404, "Producto no encontrado")));
                }
            }
            case "POST" -> {
                Producto p = validar(leerJson(ex, Producto.class));
                responder(ex, 201, dao.insertar(p));
            }
            case "PUT" -> {
                Producto p = validar(leerJson(ex, Producto.class));
                p.setIdProducto(id);
                if (!dao.actualizar(p)) {
                    throw new VentaException(404, "Producto no encontrado");
                }
                responder(ex, 200, p);
            }
            case "DELETE" -> {
                if (!dao.eliminar(id)) {
                    throw new VentaException(404, "Producto no encontrado");
                }
                responder(ex, 204, null);
            }
            default -> responderError(ex, 405, "Método no permitido");
        }
    }

    private Producto validar(Producto p) {
        if (p == null || p.getNombre() == null || p.getNombre().isBlank()) {
            throw new VentaException(400, "El nombre es obligatorio");
        }
        if (p.getPrecio() == null || p.getPrecio().signum() < 0) {
            throw new VentaException(400, "El precio debe ser mayor o igual a cero");
        }
        if (p.getCantidad() < 0) {
            throw new VentaException(400, "La cantidad no puede ser negativa");
        }
        return p;
    }
}

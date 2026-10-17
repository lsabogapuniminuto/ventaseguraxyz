package co.edu.uniminuto.ventas.controlador;

import co.edu.uniminuto.ventas.servicio.VentaException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

/** Utilidades comunes de la API: JSON, CORS y traducción de errores a códigos HTTP. */
public abstract class BaseHandler implements HttpHandler {

    protected static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @Override
    public final void handle(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        try {
            if ("OPTIONS".equals(ex.getRequestMethod())) {
                responder(ex, 204, null);
                return;
            }
            atender(ex);
        } catch (VentaException e) {
            responderError(ex, e.getCodigoHttp(), e.getMessage());
        } catch (JsonSyntaxException | NumberFormatException e) {
            responderError(ex, 400, "Datos inválidos: " + e.getMessage());
        } catch (SQLException e) {
            responderError(ex, codigoPara(e), mensajePara(e));
        } catch (Exception e) {
            e.printStackTrace();
            responderError(ex, 500, "Error interno: " + e.getMessage());
        } finally {
            ex.close();
        }
    }

    protected abstract void atender(HttpExchange ex) throws Exception;

    protected <T> T leerJson(HttpExchange ex, Class<T> tipo) throws IOException {
        try (InputStream in = ex.getRequestBody()) {
            return GSON.fromJson(new String(in.readAllBytes(), StandardCharsets.UTF_8), tipo);
        }
    }

    /** Devuelve el id al final de la ruta (/api/productos/5 -> 5) o null si no hay. */
    protected Integer idDeRuta(HttpExchange ex, String base) {
        String resto = ex.getRequestURI().getPath().substring(base.length()).replace("/", "");
        return resto.isEmpty() ? null : Integer.valueOf(resto);
    }

    protected boolean parametro(HttpExchange ex, String nombre) {
        String q = ex.getRequestURI().getQuery();
        return q != null && q.contains(nombre + "=true");
    }

    protected void responder(HttpExchange ex, int codigo, Object cuerpo) throws IOException {
        if (cuerpo == null) {
            ex.sendResponseHeaders(codigo, -1);
            return;
        }
        byte[] bytes = GSON.toJson(cuerpo).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    protected void responderError(HttpExchange ex, int codigo, String mensaje) throws IOException {
        responder(ex, codigo, Map.of("error", mensaje == null ? "Error" : mensaje));
    }

    private int codigoPara(SQLException e) {
        if (e.getErrorCode() == 3819) {           // CHECK constraint violada
            return 400;
        }
        if ("23000".equals(e.getSQLState())) {    // llave duplicada o foránea
            return 409;
        }
        return 500;
    }

    private String mensajePara(SQLException e) {
        if (e.getErrorCode() == 1451) {
            return "No se puede eliminar: el producto tiene ventas registradas";
        }
        if (e.getErrorCode() == 1062) {
            return "Ya existe un producto con ese nombre";
        }
        return "Error de base de datos: " + e.getMessage();
    }
}

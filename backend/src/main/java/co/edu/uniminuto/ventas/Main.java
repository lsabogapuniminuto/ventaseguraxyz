package co.edu.uniminuto.ventas;

import co.edu.uniminuto.ventas.conexion.Conexion;
import co.edu.uniminuto.ventas.controlador.ContabilidadHandler;
import co.edu.uniminuto.ventas.controlador.ProductoHandler;
import co.edu.uniminuto.ventas.controlador.VentaHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.Executors;

/** Punto de entrada: verifica la conexión a MySQL y levanta la API REST. */
public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection con = Conexion.getConexion()) {
            System.out.println("Conexión exitosa a MySQL: " + con.getMetaData().getURL());
        }

        int puerto = Conexion.puertoServidor();
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        server.createContext(ProductoHandler.RUTA, new ProductoHandler());
        server.createContext(VentaHandler.RUTA, new VentaHandler());
        server.createContext(ContabilidadHandler.RUTA, new ContabilidadHandler());
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("API de Venta Segura XYZ escuchando en http://localhost:" + puerto);
        System.out.println("  GET/POST/PUT/DELETE " + ProductoHandler.RUTA);
        System.out.println("  GET/POST            " + VentaHandler.RUTA);
        System.out.println("  GET                 " + ContabilidadHandler.RUTA);
    }
}

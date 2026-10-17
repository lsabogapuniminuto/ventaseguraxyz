package co.edu.uniminuto.ventas.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexiones JDBC a la base de datos "ventas" usando el conector MySQL-Java.
 * Los datos de acceso se leen de db.properties o de variables de entorno.
 */
public final class Conexion {

    private static final Properties CONFIG = cargarConfiguracion();

    private Conexion() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(
                valor("DB_URL", "db.url"),
                valor("DB_USER", "db.user"),
                valor("DB_PASSWORD", "db.password"));
    }

    public static int puertoServidor() {
        return Integer.parseInt(valor("SERVER_PORT", "server.port"));
    }

    private static String valor(String variableEntorno, String propiedad) {
        String env = System.getenv(variableEntorno);
        return env != null && !env.isBlank() ? env : CONFIG.getProperty(propiedad);
    }

    private static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream in = Conexion.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties en src/main/resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        return props;
    }
}

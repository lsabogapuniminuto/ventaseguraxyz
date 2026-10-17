package co.edu.uniminuto.ventas.servicio;

/** Error de negocio que provoca ROLLBACK y se devuelve al cliente con un código HTTP. */
public class VentaException extends RuntimeException {

    private final int codigoHttp;

    public VentaException(int codigoHttp, String mensaje) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }
}

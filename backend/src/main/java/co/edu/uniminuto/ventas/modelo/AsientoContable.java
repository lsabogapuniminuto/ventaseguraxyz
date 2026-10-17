package co.edu.uniminuto.ventas.modelo;

import java.math.BigDecimal;

/** Registro contable generado automáticamente por cada venta confirmada. */
public class AsientoContable {

    public static final String CUENTA_CAJA = "110505";
    public static final String CUENTA_INGRESOS = "413524";

    private int idAsiento;
    private int idVenta;
    private String fecha;
    private String cuentaDebito;
    private String cuentaCredito;
    private BigDecimal valor;
    private String descripcion;

    public AsientoContable() {
    }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getCuentaDebito() { return cuentaDebito; }
    public void setCuentaDebito(String cuentaDebito) { this.cuentaDebito = cuentaDebito; }

    public String getCuentaCredito() { return cuentaCredito; }
    public void setCuentaCredito(String cuentaCredito) { this.cuentaCredito = cuentaCredito; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

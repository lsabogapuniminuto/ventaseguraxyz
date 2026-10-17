package co.edu.uniminuto.ventas.modelo;

import java.math.BigDecimal;

/** Representa una fila de la tabla productos. */
public class Producto {

    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int cantidad;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String descripcion, BigDecimal precio, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}

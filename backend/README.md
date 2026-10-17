# Venta Segura XYZ – Backend

API REST en Java (NetBeans + Maven) para el caso **Empresa XYZ** de Sistemas Transaccionales (UNIMINUTO).
Gestiona ventas e inventario en MySQL usando el conector MySQL‑Java (JDBC) y garantiza la integridad
de los datos con **COMMIT** y **ROLLBACK**.

## Estructura

```
backend/
├── scripts/                         ← scripts de la base de datos (ejecutar en orden)
│   ├── 01_crear_bd.sql              base "ventas", tablas productos y ventas
│   ├── 02_datos_prueba.sql          inventario inicial
│   ├── 03_integracion_contable.sql  tabla asientos_contables y vistas
│   └── 04_pruebas_transacciones.sql demostración manual de COMMIT / ROLLBACK
├── src/main/java/co/edu/uniminuto/ventas/
│   ├── Main.java                    levanta la API en http://localhost:8080
│   ├── conexion/Conexion.java       conexión JDBC (DriverManager)
│   ├── modelo/                      Producto, Venta, AsientoContable
│   ├── dao/                         ProductoDAO, VentaDAO, AsientoContableDAO (CRUD)
│   ├── servicio/                    VentaService (transacción), IntegracionContable
│   └── controlador/                 endpoints REST
├── src/main/resources/db.properties
└── pom.xml
```

## Requisitos

- JDK 17 o superior
- Apache NetBeans (o Maven 3.9+)
- MySQL 8.0.16 o superior (necesario para que se apliquen las restricciones `CHECK`)

## Puesta en marcha

1. Ejecute los scripts de `scripts/` en MySQL Workbench, en orden (01 → 02 → 03).
2. Edite `src/main/resources/db.properties` con su usuario y contraseña de MySQL,
   o defina las variables de entorno `DB_USER` y `DB_PASSWORD`.
   **No suba su contraseña real a GitHub.**
3. En NetBeans: *File → Open Project* → seleccione la carpeta `backend` → *Run Project* (F6).
   Desde consola: `mvn package` y luego `java -jar target/ventas-xyz-backend-1.0.0.jar`.
4. La consola debe mostrar `Conexión exitosa a MySQL` y `API ... escuchando en http://localhost:8080`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista el inventario |
| GET | `/api/productos/{id}` | Consulta un producto |
| POST | `/api/productos` | Crea un producto |
| PUT | `/api/productos/{id}` | Actualiza un producto o su stock |
| DELETE | `/api/productos/{id}` | Elimina un producto |
| GET | `/api/ventas` | Historial de ventas |
| POST | `/api/ventas` | Registra una venta (transacción) |
| POST | `/api/ventas?simularErrorContable=true` | Fuerza un fallo para demostrar el ROLLBACK |
| GET | `/api/contabilidad/asientos` | Asientos contables (integración con contabilidad) |

Ejemplo de venta:

```json
POST /api/ventas
{ "idProducto": 3, "cantidad": 2, "precioVenta": 449000 }
```

## Cómo funciona la transacción

`VentaService.registrarVenta()`:

1. `con.setAutoCommit(false)` – inicia la transacción.
2. `SELECT ... FOR UPDATE` – bloquea el producto y valida el stock.
3. `INSERT INTO ventas` – registra la venta.
4. `UPDATE productos` – descuenta el inventario.
5. `INSERT INTO asientos_contables` – integración contable.
6. `con.commit()` si todo salió bien; `con.rollback()` ante cualquier error.

En la consola de NetBeans se imprime cada paso (`[TX] COMMIT` / `[TX] ROLLBACK`).

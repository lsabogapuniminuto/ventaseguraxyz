# Venta Segura XYZ – Frontend

Interfaz web en **Angular 19** para los vendedores de la Empresa XYZ (Sistemas Transaccionales, UNIMINUTO).
Consume la API REST del repositorio backend.

## Pantallas

| Ruta | Componente | Qué hace |
|---|---|---|
| `/ventas` | `registro-venta` | Registra ventas y muestra el historial. Permite simular un fallo contable para ver el ROLLBACK. |
| `/inventario` | `inventario` | Crea, edita y elimina productos y su stock. |
| `/contabilidad` | `contabilidad` | Muestra los asientos contables generados por cada venta. |

## Estructura

```
frontend/src/app/
├── components/
│   ├── registro-venta/
│   ├── inventario/
│   └── contabilidad/
├── models/modelos.ts
├── services/api.service.ts    ← URL del backend (http://localhost:8080/api)
├── app.routes.ts
└── app.config.ts
```

## Ejecutar

Requisitos: Node.js 20.11 o superior.

```bash
npm install
npx ng serve -o
```

La aplicación abre en http://localhost:4200. El backend debe estar ejecutándose en el puerto 8080.

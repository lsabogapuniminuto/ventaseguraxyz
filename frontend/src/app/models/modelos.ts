export interface Producto {
  idProducto?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  cantidad: number;
}

export interface Venta {
  idVenta?: number;
  fecha?: string;
  idProducto: number;
  nombreProducto?: string;
  cantidad: number;
  precioVenta?: number;
  total?: number;
}

export interface AsientoContable {
  idAsiento: number;
  idVenta: number;
  fecha: string;
  cuentaDebito: string;
  cuentaCredito: string;
  valor: number;
  descripcion: string;
}

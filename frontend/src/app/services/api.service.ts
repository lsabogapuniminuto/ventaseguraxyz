import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AsientoContable, Producto, Venta } from '../models/modelos';

/** URL del backend Java (ver backend/src/main/resources/db.properties -> server.port). */
export const API_URL = 'http://localhost:8081/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  // ---------- Inventario ----------
  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${API_URL}/productos`);
  }

  crearProducto(p: Producto): Observable<Producto> {
    return this.http.post<Producto>(`${API_URL}/productos`, p);
  }

  actualizarProducto(p: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${API_URL}/productos/${p.idProducto}`, p);
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/productos/${id}`);
  }

  // ---------- Ventas (transacción en el backend) ----------
  listarVentas(): Observable<Venta[]> {
    return this.http.get<Venta[]>(`${API_URL}/ventas`);
  }

  registrarVenta(v: Venta, simularErrorContable = false): Observable<Venta> {
    const params = simularErrorContable ? new HttpParams().set('simularErrorContable', 'true') : undefined;
    return this.http.post<Venta>(`${API_URL}/ventas`, v, { params });
  }

  // ---------- Integración contable ----------
  listarAsientos(): Observable<AsientoContable[]> {
    return this.http.get<AsientoContable[]>(`${API_URL}/contabilidad/asientos`);
  }
}

/** Extrae el mensaje que envía el backend en { "error": "..." }. */
export function mensajeDeError(e: HttpErrorResponse): string {
  if (e.status === 0) {
    return 'No hay conexión con el backend (¿está ejecutándose en el puerto 8080?)';
  }
  return e.error?.error ?? `Error ${e.status}`;
}

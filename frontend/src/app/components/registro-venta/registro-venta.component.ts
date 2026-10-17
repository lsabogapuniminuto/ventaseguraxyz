import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Producto, Venta } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';

@Component({
  selector: 'app-registro-venta',
  imports: [FormsModule, CurrencyPipe],
  templateUrl: './registro-venta.component.html',
})
export class RegistroVentaComponent implements OnInit {
  private readonly api = inject(ApiService);

  productos = signal<Producto[]>([]);
  ventas = signal<Venta[]>([]);
  mensaje = signal<{ tipo: 'exito' | 'error'; texto: string } | null>(null);
  guardando = signal(false);

  idProducto: number | null = null;
  cantidad = 1;
  precioVenta: number | null = null;
  simularErrorContable = false;

  productoSeleccionado = computed(() => this.productos().find((p) => p.idProducto === this.idSeleccionado()));
  private idSeleccionado = signal<number | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarProductos().subscribe({
      next: (p) => this.productos.set(p),
      error: (e: HttpErrorResponse) => this.mensaje.set({ tipo: 'error', texto: mensajeDeError(e) }),
    });
    this.api.listarVentas().subscribe({ next: (v) => this.ventas.set(v) });
  }

  alCambiarProducto(id: number | null): void {
    this.idSeleccionado.set(id);
    this.precioVenta = this.productoSeleccionado()?.precio ?? null;
  }

  get total(): number {
    return (this.precioVenta ?? 0) * (this.cantidad ?? 0);
  }

  registrar(): void {
    if (!this.idProducto) {
      return;
    }
    const venta: Venta = {
      idProducto: this.idProducto,
      cantidad: this.cantidad,
      precioVenta: this.precioVenta ?? undefined,
    };
    this.guardando.set(true);
    this.mensaje.set(null);
    this.api.registrarVenta(venta, this.simularErrorContable).subscribe({
      next: (v) => {
        this.mensaje.set({
          tipo: 'exito',
          texto: `COMMIT: venta #${v.idVenta} registrada, inventario y contabilidad actualizados.`,
        });
        this.guardando.set(false);
        this.cantidad = 1;
        this.cargar();
      },
      error: (e: HttpErrorResponse) => {
        this.mensaje.set({
          tipo: 'error',
          texto: `ROLLBACK: ${mensajeDeError(e)}. No se guardó ningún cambio.`,
        });
        this.guardando.set(false);
        this.cargar();
      },
    });
  }
}

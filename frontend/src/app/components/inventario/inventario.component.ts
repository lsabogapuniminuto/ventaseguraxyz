import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Producto } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';

const VACIO: Producto = { nombre: '', descripcion: '', precio: 0, cantidad: 0 };

@Component({
  selector: 'app-inventario',
  imports: [FormsModule, CurrencyPipe],
  templateUrl: './inventario.component.html',
})
export class InventarioComponent implements OnInit {
  private readonly api = inject(ApiService);

  productos = signal<Producto[]>([]);
  mensaje = signal<{ tipo: 'exito' | 'error'; texto: string } | null>(null);
  form: Producto = { ...VACIO };

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarProductos().subscribe({
      next: (p) => this.productos.set(p),
      error: (e: HttpErrorResponse) => this.error(e),
    });
  }

  editar(p: Producto): void {
    this.form = { ...p };
    this.mensaje.set(null);
  }

  cancelar(): void {
    this.form = { ...VACIO };
  }

  guardar(): void {
    const peticion = this.form.idProducto
      ? this.api.actualizarProducto(this.form)
      : this.api.crearProducto(this.form);
    peticion.subscribe({
      next: (p) => {
        this.mensaje.set({ tipo: 'exito', texto: `Producto "${p.nombre}" guardado.` });
        this.cancelar();
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.error(e),
    });
  }

  eliminar(p: Producto): void {
    if (!confirm(`¿Eliminar "${p.nombre}"?`)) {
      return;
    }
    this.api.eliminarProducto(p.idProducto!).subscribe({
      next: () => {
        this.mensaje.set({ tipo: 'exito', texto: `Producto "${p.nombre}" eliminado.` });
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.error(e),
    });
  }

  private error(e: HttpErrorResponse): void {
    this.mensaje.set({ tipo: 'error', texto: mensajeDeError(e) });
  }
}

import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AsientoContable } from '../../models/modelos';
import { ApiService, mensajeDeError } from '../../services/api.service';

@Component({
  selector: 'app-contabilidad',
  imports: [CurrencyPipe],
  templateUrl: './contabilidad.component.html',
})
export class ContabilidadComponent implements OnInit {
  private readonly api = inject(ApiService);

  asientos = signal<AsientoContable[]>([]);
  error = signal<string | null>(null);
  totalIngresos = computed(() => this.asientos().reduce((suma, a) => suma + a.valor, 0));

  ngOnInit(): void {
    this.api.listarAsientos().subscribe({
      next: (a) => this.asientos.set(a),
      error: (e: HttpErrorResponse) => this.error.set(mensajeDeError(e)),
    });
  }
}

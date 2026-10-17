import { Routes } from '@angular/router';
import { ContabilidadComponent } from './components/contabilidad/contabilidad.component';
import { InventarioComponent } from './components/inventario/inventario.component';
import { RegistroVentaComponent } from './components/registro-venta/registro-venta.component';

export const routes: Routes = [
  { path: '', redirectTo: 'ventas', pathMatch: 'full' },
  { path: 'ventas', component: RegistroVentaComponent, title: 'Registrar venta' },
  { path: 'inventario', component: InventarioComponent, title: 'Inventario' },
  { path: 'contabilidad', component: ContabilidadComponent, title: 'Contabilidad' },
  { path: '**', redirectTo: 'ventas' },
];

import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.LoginComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/main-layout/main-layout').then(
        (m) => m.MainLayoutComponent
      ),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./features/users/users-list/users-list').then(
            (m) => m.UsersListComponent
          ),
      },
      {
        path: 'produtos',
        loadComponent: () =>
          import('./features/products/products-list/products-list').then(
            (m) => m.ProductsListComponent
          ),
      },
      {
        path: 'rotinas',
        loadComponent: () =>
          import('./features/rotinas/rotinas-list/rotinas-list').then(
            (m) => m.RotinasListComponent
          ),
      },
      {
        path: 'perfis',
        loadComponent: () =>
          import('./features/perfis/perfis-list/perfis-list').then(
            (m) => m.PerfisListComponent
          ),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.LoginComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password').then(
        (m) => m.ForgotPasswordComponent
      ),
  },
  {
    path: 'change-password',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/auth/change-password/change-password').then(
        (m) => m.ChangePasswordComponent
      ),
  },
  {
    path: 'password-reset-success',
    loadComponent: () =>
      import('./features/auth/password-reset-success/password-reset-success').then(
        (m) => m.PasswordResetSuccessComponent
      ),
  },
  {
    path: 'planos',
    loadComponent: () =>
      import('./features/public/plans/plans').then((m) => m.PlansComponent),
  },
  {
    path: 'contato',
    loadComponent: () =>
      import('./features/public/contact/contact').then(
        (m) => m.ContactComponent,
      ),
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
        path: 'perfil',
        loadComponent: () =>
          import('./features/profile/profile').then((m) => m.ProfileComponent),
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
        path: 'produtos/cadastrar',
        loadComponent: () =>
          import('./features/products/product-base-create/product-base-create').then(
            (m) => m.ProductBaseCreateComponent
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

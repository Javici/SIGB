import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // Ruta raíz → login
  { path: '', redirectTo: 'catalog', pathMatch: 'full' },

  // Pública
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // Catálogo (accesible sin autenticación)
  {
    path: 'catalog',
    loadComponent: () =>
      import('./features/catalog/catalog.component').then(m => m.CatalogComponent)
  },

  // Perfil (requiere login)
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },

  // Panel de bibliotecario (LIBRARIAN o ADMIN)
  {
    path: 'librarian',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] },
    loadComponent: () =>
      import('./features/librarian/librarian.component').then(m => m.LibrarianComponent)
  },

  // Panel de administrador (solo ADMIN)
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () =>
      import('./features/admin/admin.component').then(m => m.AdminComponent)
  },

  // Fallback
  { path: '**', redirectTo: 'login' }
];

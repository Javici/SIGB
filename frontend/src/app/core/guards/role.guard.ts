import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

/**
 * Guard de roles.
 * Uso en rutas:
 *   canActivate: [roleGuard],
 *   data: { roles: ['ADMIN', 'LIBRARIAN'] }
 */
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  const allowedRoles: Role[] = route.data['roles'] ?? [];

  if (auth.hasRole(...allowedRoles)) {
    return true;
  }
  return router.createUrlTree(['/login']);
};

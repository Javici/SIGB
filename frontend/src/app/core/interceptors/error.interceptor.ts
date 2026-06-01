import { HttpInterceptorFn, HttpStatusCode } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor funcional (Angular 19 style).
 * Captura respuestas 401 Unauthorized y fuerza el cierre de sesión,
 * redirigiendo al usuario a /login para que obtenga un token nuevo.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError(err => {
      if (err.status === HttpStatusCode.Unauthorized) {
        authService.logout();
      }
      return throwError(() => err);
    })
  );
};

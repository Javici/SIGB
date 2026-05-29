import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest, Role } from '../models/user.model';

/**
 * Servicio de autenticación.
 *
 * Almacena el JWT en localStorage y expone señales reactivas (Angular 19 Signals)
 * para que los Guards y componentes puedan reaccionar a cambios de sesión.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = '/api/v1/auth';

  // Señales reactivas (Angular Signals – sustituyen a BehaviorSubject)
  readonly isLoggedIn  = signal<boolean>(this.hasToken());
  readonly currentRole = signal<Role | null>(this.getStoredRole());
  readonly username    = signal<string | null>(this.getStoredUsername());

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, req).pipe(
      tap(res => this.storeSession(res))
    );
  }

  register(req: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/register`, req).pipe(
      tap(res => this.storeSession(res))
    );
  }

  logout(): void {
    localStorage.removeItem('sigb_token');
    localStorage.removeItem('sigb_role');
    localStorage.removeItem('sigb_username');
    this.isLoggedIn.set(false);
    this.currentRole.set(null);
    this.username.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('sigb_token');
  }

  hasRole(...roles: Role[]): boolean {
    const role = this.currentRole();
    return role !== null && roles.includes(role);
  }

  // ── Privados ─────────────────────────────────────────────────────

  private storeSession(res: LoginResponse): void {
    localStorage.setItem('sigb_token',    res.token);
    localStorage.setItem('sigb_role',     res.role);
    localStorage.setItem('sigb_username', res.username);
    this.isLoggedIn.set(true);
    this.currentRole.set(res.role);
    this.username.set(res.username);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('sigb_token');
  }

  private getStoredRole(): Role | null {
    return localStorage.getItem('sigb_role') as Role | null;
  }

  private getStoredUsername(): string | null {
    return localStorage.getItem('sigb_username');
  }
}

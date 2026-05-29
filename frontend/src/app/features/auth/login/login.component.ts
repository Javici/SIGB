import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

type Mode = 'login' | 'register';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  mode    = signal<Mode>('login');
  loading = signal(false);
  error   = signal<string | null>(null);

  loginForm: FormGroup;
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email:    ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  switchMode(m: Mode): void {
    this.mode.set(m);
    this.error.set(null);
  }

  onLogin(): void {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    this.error.set(null);

    this.auth.login(this.loginForm.value).subscribe({
      next: () => this.router.navigate(['/profile']),
      error: (err) => {
        this.error.set(err.error?.message ?? 'Credenciales incorrectas.');
        this.loading.set(false);
      }
    });
  }

  onRegister(): void {
    if (this.registerForm.invalid) return;
    this.loading.set(true);
    this.error.set(null);

    this.auth.register(this.registerForm.value).subscribe({
      next: () => this.router.navigate(['/profile']),
      error: (err) => {
        this.error.set(err.error?.message ?? 'Error al registrar el usuario.');
        this.loading.set(false);
      }
    });
  }
}

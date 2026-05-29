import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { User, Role } from '../../core/models/user.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

  users   = signal<User[]>([]);
  loading = signal(true);
  success = signal<string | null>(null);
  error   = signal<string | null>(null);

  readonly roles: Role[] = ['ADMIN', 'LIBRARIAN', 'READER'];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<User[]>('/api/v1/admin/users').subscribe({
      next: users => { this.users.set(users); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  changeRole(userId: number, role: Role): void {
    this.http.put<User>(`/api/v1/admin/users/${userId}/role?role=${role}`, null).subscribe({
      next: u => {
        this.users.update(list => list.map(x => x.id === u.id ? u : x));
        this.success.set(`Rol de ${u.username} cambiado a ${u.role}.`);
      },
      error: err => this.error.set(err.error?.message ?? 'Error al cambiar el rol.')
    });
  }

  toggleActive(user: User): void {
    const newActive = !user.active;
    this.http.put<User>(`/api/v1/admin/users/${user.id}/active?active=${newActive}`, null).subscribe({
      next: u => {
        this.users.update(list => list.map(x => x.id === u.id ? u : x));
        this.success.set(`Usuario ${u.username} ${u.active ? 'activado' : 'desactivado'}.`);
      },
      error: err => this.error.set(err.error?.message ?? 'Error al cambiar estado.')
    });
  }
}

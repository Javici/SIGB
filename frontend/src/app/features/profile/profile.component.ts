import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoanService } from '../../core/services/loan.service';
import { AuthService } from '../../core/services/auth.service';
import { Loan, Reservation } from '../../core/models/loan.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {

  loans        = signal<Loan[]>([]);
  reservations = signal<Reservation[]>([]);
  loading      = signal(true);
  success      = signal<string | null>(null);
  error        = signal<string | null>(null);

  profileForm: FormGroup;

  constructor(
    private loanService: LoanService,
    readonly auth: AuthService,
    private fb: FormBuilder
  ) {
    this.profileForm = this.fb.group({
      email:    ['', [Validators.email]],
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loanService.getMyReservations().subscribe({
      next: res => this.reservations.set(res)
    });
    this.loading.set(false);
  }

  cancelReservation(id: number): void {
    this.loanService.cancelReservation(id).subscribe({
      next: () => {
        this.success.set('Reserva cancelada.');
        this.reservations.update(list => list.filter(r => r.id !== id));
      },
      error: err => this.error.set(err.error?.message ?? 'Error al cancelar.')
    });
  }

  get totalFines(): number {
    return this.loans().reduce((acc, l) => acc + l.fine, 0);
  }
}

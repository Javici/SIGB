import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Loan, Reservation } from '../models/loan.model';

@Injectable({ providedIn: 'root' })
export class LoanService {

  private readonly LOAN_API        = '/api/v1/librarian/loans';
  private readonly RESERVATION_API = '/api/v1/reservations';

  constructor(private http: HttpClient) {}

  // ── Préstamos ─────────────────────────────────────────────────────

  getAllLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(this.LOAN_API);
  }

  getLoansByUser(userId: number): Observable<Loan[]> {
    return this.http.get<Loan[]>(`${this.LOAN_API}/user/${userId}`);
  }

  returnBook(loanId: number): Observable<Loan> {
    return this.http.put<Loan>(`${this.LOAN_API}/${loanId}/return`, null);
  }

  // ── Reservas ──────────────────────────────────────────────────────

  getMyReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.RESERVATION_API}/my`);
  }

  getPendingReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>('/api/v1/librarian/reservations/pending');
  }

  acceptReservation(id: number): Observable<Loan> {
    return this.http.put<Loan>(`/api/v1/librarian/reservations/${id}/accept`, null);
  }

  denyReservation(id: number): Observable<void> {
    return this.http.put<void>(`/api/v1/librarian/reservations/${id}/deny`, null);
  }

  getQueueForBook(bookId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.RESERVATION_API}/book/${bookId}`);
  }

  createReservation(bookId: number): Observable<Reservation> {
    const params = new HttpParams().set('bookId', bookId);
    return this.http.post<Reservation>(this.RESERVATION_API, null, { params });
  }

  cancelReservation(reservationId: number): Observable<void> {
    return this.http.delete<void>(`${this.RESERVATION_API}/${reservationId}`);
  }
}

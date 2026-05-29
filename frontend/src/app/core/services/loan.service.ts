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

  createLoan(userId: number, bookId: number): Observable<Loan> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('bookId', bookId);
    return this.http.post<Loan>(this.LOAN_API, null, { params });
  }

  returnBook(loanId: number): Observable<Loan> {
    return this.http.put<Loan>(`${this.LOAN_API}/${loanId}/return`, null);
  }

  // ── Reservas ──────────────────────────────────────────────────────

  getMyReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.RESERVATION_API}/my`);
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

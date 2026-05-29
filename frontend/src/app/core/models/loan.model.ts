export type LoanStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE';
export type ReservationStatus = 'PENDING' | 'NOTIFIED' | 'FULFILLED' | 'CANCELLED';

export interface Loan {
  id: number;
  userId: number;
  username: string;
  bookId: number;
  bookTitle: string;
  loanDate: string;
  dueDate: string;
  returnDate: string | null;
  status: LoanStatus;
  overdueDays: number;
  fine: number;
}

export interface Reservation {
  id: number;
  userId: number;
  username: string;
  bookId: number;
  bookTitle: string;
  reservationDate: string;
  status: ReservationStatus;
  queuePosition: number;
}

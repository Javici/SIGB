import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoanService } from '../../core/services/loan.service';
import { BookService } from '../../core/services/book.service';
import { Loan, Reservation } from '../../core/models/loan.model';
import { Book } from '../../core/models/book.model';

@Component({
  selector: 'app-librarian',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './librarian.component.html'
})
export class LibrarianComponent implements OnInit {

  loans                = signal<Loan[]>([]);
  books                = signal<Book[]>([]);
  pendingReservations  = signal<Reservation[]>([]);
  loading              = signal(true);
  success  = signal<string | null>(null);
  error    = signal<string | null>(null);

  bookForm: FormGroup;
  editingBook = signal<Book | null>(null);

  constructor(
    private loanService: LoanService,
    private bookService: BookService,
    private fb: FormBuilder
  ) {
    this.bookForm = this.fb.group({
      title:        ['', Validators.required],
      author:       ['', Validators.required],
      isbn:         [''],
      category:     [''],
      description:  [''],
      totalCopies:  [1, [Validators.required, Validators.min(1)]],
      availableCopies: [1],
      publishedYear: [new Date().getFullYear()]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.loanService.getAllLoans().subscribe({
      next: loans => this.loans.set(loans),
      error: err => this.error.set(err.error?.message ?? 'Error al cargar los préstamos.')
    });
    this.loanService.getPendingReservations().subscribe({
      next: reservations => this.pendingReservations.set(reservations),
      error: err => this.error.set(err.error?.message ?? 'Error al cargar reservas pendientes.')
    });
    this.bookService.getAll().subscribe({
      next: books => { this.books.set(books); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  acceptReservation(id: number): void {
    this.loanService.acceptReservation(id).subscribe({
      next: loan => {
        this.pendingReservations.update(list => list.filter(r => r.id !== id));
        this.loans.update(list => [loan, ...list]);
        this.success.set(`Reserva aceptada. Préstamo creado: "${loan.bookTitle}"`);
      },
      error: err => this.error.set(err.error?.message ?? 'Error al aceptar la reserva.')
    });
  }

  denyReservation(id: number): void {
    this.loanService.denyReservation(id).subscribe({
      next: () => {
        this.pendingReservations.update(list => list.filter(r => r.id !== id));
        this.success.set('Reserva denegada. La copia ha sido devuelta al catálogo si procedía.');
      },
      error: err => this.error.set(err.error?.message ?? 'Error al denegar la reserva.')
    });
  }

  returnBook(loanId: number): void {
    this.loanService.returnBook(loanId).subscribe({
      next: updated => {
        this.loans.update(list => list.map(l => l.id === loanId ? updated : l));
        this.success.set('Devolución registrada. Se notificará al siguiente en cola si existe.');
      },
      error: err => this.error.set(err.error?.message ?? 'Error al registrar la devolución.')
    });
  }

  startEditBook(book: Book): void {
    this.editingBook.set(book);
    this.bookForm.patchValue(book);
  }

  saveBook(): void {
    if (this.bookForm.invalid) return;
    const editing = this.editingBook();

    if (editing?.id != null) {
      this.bookService.update(editing.id, this.bookForm.value).subscribe({
        next: b => {
          this.books.update(list => list.map(x => x.id === b.id ? b : x));
          this.success.set('Libro actualizado.');
          this.editingBook.set(null);
          this.bookForm.reset();
        },
        error: err => this.error.set(err.error?.message ?? 'Error al actualizar.')
      });
    } else {
      this.bookService.create(this.bookForm.value).subscribe({
        next: b => {
          this.books.update(list => [b, ...list]);
          this.success.set('Libro añadido al catálogo.');
          this.bookForm.reset({ totalCopies: 1, availableCopies: 1 });
        },
        error: err => this.error.set(err.error?.message ?? 'Error al crear el libro.')
      });
    }
  }

  activeLoans(): Loan[] {
    return this.loans().filter(l => l.status === 'ACTIVE');
  }
}

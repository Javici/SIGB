import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { BookService } from '../../core/services/book.service';
import { LoanService } from '../../core/services/loan.service';
import { AuthService } from '../../core/services/auth.service';
import { Book } from '../../core/models/book.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './catalog.component.html'
})
export class CatalogComponent implements OnInit {

  books     = signal<Book[]>([]);
  loading   = signal(true);
  error     = signal<string | null>(null);
  success   = signal<string | null>(null);

  searchForm: FormGroup;

  constructor(
    private bookService: BookService,
    private loanService: LoanService,
    readonly auth: AuthService,
    private fb: FormBuilder
  ) {
    this.searchForm = this.fb.group({
      title:        [''],
      author:       [''],
      isbn:         [''],
      category:     [''],
      availableOnly:[false]
    });
  }

  ngOnInit(): void {
    this.loadBooks();

    // Búsqueda reactiva: dispara petición 400 ms después del último teclazo
    this.searchForm.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(v => {
        this.loading.set(true);
        const hasFilter = v.title || v.author || v.isbn || v.category || v.availableOnly;
        return hasFilter ? this.bookService.search(v) : this.bookService.getAll();
      })
    ).subscribe({
      next: books => { this.books.set(books); this.loading.set(false); },
      error: () => { this.error.set('Error al buscar libros.'); this.loading.set(false); }
    });
  }

  loadBooks(): void {
    this.loading.set(true);
    this.bookService.getAll().subscribe({
      next: books => { this.books.set(books); this.loading.set(false); },
      error: () => { this.error.set('Error al cargar el catálogo.'); this.loading.set(false); }
    });
  }

  reserve(bookId: number): void {
    this.loanService.createReservation(bookId).subscribe({
      next: r => {
        this.success.set(`Reserva creada. Tu posición en cola: ${r.queuePosition}`);
        this.books.update(list =>
          list.map(b => b.id === bookId && b.availableCopies > 0
            ? { ...b, availableCopies: b.availableCopies - 1 }
            : b
          )
        );
      },
      error: err => this.error.set(err.error?.message ?? 'Error al crear la reserva.')
    });
  }

  clearMessages(): void {
    this.error.set(null);
    this.success.set(null);
  }
}

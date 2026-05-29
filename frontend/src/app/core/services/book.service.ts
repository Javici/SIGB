import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book, BookSearchParams } from '../models/book.model';

@Injectable({ providedIn: 'root' })
export class BookService {

  private readonly API = '/api/v1/books';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Book[]> {
    return this.http.get<Book[]>(this.API);
  }

  getById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.API}/${id}`);
  }

  /**
   * Búsqueda multifiltro – sólo envía los parámetros que no sean vacíos.
   */
  search(params: BookSearchParams): Observable<Book[]> {
    let httpParams = new HttpParams();
    if (params.title)         httpParams = httpParams.set('title',         params.title);
    if (params.author)        httpParams = httpParams.set('author',        params.author);
    if (params.isbn)          httpParams = httpParams.set('isbn',          params.isbn);
    if (params.category)      httpParams = httpParams.set('category',      params.category);
    if (params.availableOnly) httpParams = httpParams.set('availableOnly', 'true');
    return this.http.get<Book[]>(`${this.API}/search`, { params: httpParams });
  }

  create(book: Omit<Book, 'id'>): Observable<Book> {
    return this.http.post<Book>(this.API, book);
  }

  update(id: number, book: Partial<Book>): Observable<Book> {
    return this.http.put<Book>(`${this.API}/${id}`, book);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}

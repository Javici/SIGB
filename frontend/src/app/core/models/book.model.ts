export interface Book {
  id: number | null;
  title: string;
  author: string;
  isbn: string | null;
  category: string | null;
  description: string | null;
  totalCopies: number;
  availableCopies: number;
  publishedYear: number;
}

export interface BookSearchParams {
  title?: string;
  author?: string;
  isbn?: string;
  category?: string;
  availableOnly?: boolean;
}

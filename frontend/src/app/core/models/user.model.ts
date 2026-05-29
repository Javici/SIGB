export type Role = 'ADMIN' | 'LIBRARIAN' | 'READER';

export interface User {
  id: number;
  username: string;
  email: string;
  role: Role;
  active: boolean;
  sanctioned: boolean;
  sanctionedUntil: string | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  role: Role;
}

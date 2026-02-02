import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

type RegisterRequest = { email: string; username: string; password: string };
type LoginRequest = { identifier: string; password: string };
type LoginResponse = { accessToken: string };
type IdResponse = { userId: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = '/api/auth'; // PROXY NGINX => auth-service

  constructor(private http: HttpClient) {}

  register(req: RegisterRequest) {
    return this.http.post<IdResponse>(`${this.baseUrl}/register`, req);
  }

  login(req: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, req).pipe(
      tap(res => localStorage.setItem('accessToken', res.accessToken))
    );
  }

  logout() {
    localStorage.removeItem('accessToken');
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}

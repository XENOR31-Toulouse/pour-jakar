import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

type RegisterRequest = { email: string; username: string; password: string };
type LoginRequest = { identifier: string; password: string };
type LoginResponse = { accessToken: string; refreshToken: string };
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
    tap(res => {
      localStorage.setItem('accessToken', res.accessToken);
      localStorage.setItem('refreshToken', res.refreshToken);
    })
  );
}

refresh() {
  const rt = localStorage.getItem('refreshToken');
  return this.http.post<LoginResponse>(`${this.baseUrl}/refresh`, { refreshToken: rt }).pipe(
    tap(res => {
      localStorage.setItem('accessToken', res.accessToken);
      localStorage.setItem('refreshToken', res.refreshToken);
    })
  );
}

requestPasswordReset(email: string) {
  return this.http.post(`/api/auth/password/reset-request`, { email });
}

resetPassword(token: string, newPassword: string) {
  return this.http.post(`/api/auth/password/reset`, { token, newPassword });
}

adminCreateUser(email: string, username: string, password: string) {
  return this.http.post<{ userId: string }>(`/api/admin/users`, { email, username, password });
}



logout() {
  const rt = localStorage.getItem('refreshToken');
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  // optionnel: notifier backend
  return this.http.post(`${this.baseUrl}/logout`, { refreshToken: rt });
}

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

getRole(): string | null {
  const token = this.getToken();
  if (!token) return null;
  try {
    const payloadPart = token.split('.')[1];
    const payloadJson = atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(payloadJson)?.role ?? null;
  } catch {
    return null;
  }
}


}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

type RegisterRequest = { email: string; username: string; password: string };
type LoginRequest = { identifier: string; password: string };
type LoginResponse = { accessToken: string; refreshToken: string };
type IdResponse = { userId: string };
type EmployeeDto = { id: string; email: string; username: string; createdAt: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
private readonly baseUrl = '/api/auth/auth';

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



adminListEmployees() {
  return this.http.get<EmployeeDto[]>(`/api/auth/admin/users/employees`);
}

adminCreateEmployee(email: string, username: string, password: string) {
  return this.http.post<{ userId: string }>(`/api/auth/admin/users/employees`, { email, username, password });
}

adminDeleteEmployee(id: string) {
  return this.http.delete(`/api/auth/admin/users/employees/${id}`);
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



logoutAndClear() {
  const rt = localStorage.getItem('refreshToken');

  // clear immediately
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');

  // optional backend revoke (subscribe from caller)
  return this.http.post(`/api/auth/logout`, { refreshToken: rt });
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

getUserId(): string | null {
  const token = this.getToken();
  if (!token) return null;
  try {
    const payloadPart = token.split('.')[1];
    const payloadJson = atob(payloadPart.replace(/-/g, '+').replace(/_/g, '/'));
    const payload = JSON.parse(payloadJson);
    return payload?.sub ?? null; // JWT subject = userId
  } catch {
    return null;
  }
}


}

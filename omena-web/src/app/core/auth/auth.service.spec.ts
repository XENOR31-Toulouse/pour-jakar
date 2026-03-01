import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

function b64url(obj: unknown): string {
  const json = JSON.stringify(obj);
  const b64 = btoa(json).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
  return b64;
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('login stores accessToken and refreshToken', () => {
    service.login({ identifier: 'u', password: 'p' }).subscribe();

    const req = httpMock.expectOne('/api/auth/auth/login');
    expect(req.request.method).toBe('POST');

    req.flush({ accessToken: 'AT', refreshToken: 'RT' });

    expect(localStorage.getItem('accessToken')).toBe('AT');
    expect(localStorage.getItem('refreshToken')).toBe('RT');
  });

  it('refresh sends refreshToken from localStorage and overwrites tokens', () => {
    localStorage.setItem('refreshToken', 'OLD_RT');

    service.refresh().subscribe();

    const req = httpMock.expectOne('/api/auth/auth/refresh');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ refreshToken: 'OLD_RT' });

    req.flush({ accessToken: 'NEW_AT', refreshToken: 'NEW_RT' });

    expect(localStorage.getItem('accessToken')).toBe('NEW_AT');
    expect(localStorage.getItem('refreshToken')).toBe('NEW_RT');
  });

  it('logoutAndClear clears tokens immediately and posts refresh token', () => {
    localStorage.setItem('accessToken', 'AT');
    localStorage.setItem('refreshToken', 'RT');

    service.logoutAndClear().subscribe();

    // tokens cleared immediately
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(localStorage.getItem('refreshToken')).toBeNull();

    const req = httpMock.expectOne('/api/auth/logout');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ refreshToken: 'RT' });

    req.flush({});
  });

  it('getRole decodes JWT payload role', () => {
    const token = `h.${b64url({ role: 'ADMIN', sub: 'user-1' })}.s`;
    localStorage.setItem('accessToken', token);

    expect(service.getRole()).toBe('ADMIN');
  });

  it('getUserId decodes JWT payload sub', () => {
    const token = `h.${b64url({ role: 'EMPLOYEE', sub: 'abc-123' })}.s`;
    localStorage.setItem('accessToken', token);

    expect(service.getUserId()).toBe('abc-123');
  });

  it('isLoggedIn is false when no token, true when token exists', () => {
    expect(service.isLoggedIn()).toBe(false);

    localStorage.setItem('accessToken', 'AT');
    expect(service.isLoggedIn()).toBe(true);
  });

  it('adminCreateEmployee posts payload to correct endpoint', () => {
    service.adminCreateEmployee('a@b.com', 'bob', 'pw').subscribe();

    const req = httpMock.expectOne('/api/auth/admin/users/employees');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'a@b.com', username: 'bob', password: 'pw' });

    req.flush({ userId: '1' });
  });
});

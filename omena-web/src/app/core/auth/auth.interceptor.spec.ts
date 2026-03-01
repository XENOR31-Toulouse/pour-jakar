import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  function setup(authMock: Partial<AuthService>, routerMock?: Partial<Router>) {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authMock },
        {
          provide: Router,
          useValue:
            routerMock ??
            ({
              navigateByUrl: () => Promise.resolve(true),
              parseUrl: (u: string) => ({ url: u }),
            } as Partial<Router>),
        },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  }

  afterEach(() => {
    if (httpMock) httpMock.verify();
  });

  it('adds Authorization header when token exists', () => {
    setup({
      getToken: () => 'AT',
      getRefreshToken: () => 'RT',
      refresh: () => of({ accessToken: 'AT', refreshToken: 'RT' }),
    });

    http.get('/api/site/admin/worksites').subscribe();

    const req = httpMock.expectOne('/api/site/admin/worksites');
    expect(req.request.headers.get('Authorization')).toBe('Bearer AT');
    req.flush([]);
  });

  it('on 401 triggers refresh then retries original request with new token', () => {
    let currentToken = 'OLD';
    const auth = {
      getToken: () => currentToken,
      getRefreshToken: () => 'RT',
      refresh: () => {
        currentToken = 'NEW';
        return of({ accessToken: 'NEW', refreshToken: 'RT2' });
      },
      logoutAndClear: () => of({}),
    } as Partial<AuthService>;

    setup(auth);

    http.get('/api/site/admin/worksites').subscribe();

    const first = httpMock.expectOne('/api/site/admin/worksites');
    expect(first.request.headers.get('Authorization')).toBe('Bearer OLD');
    first.flush({}, { status: 401, statusText: 'Unauthorized' });

    const retry = httpMock.expectOne('/api/site/admin/worksites');
    expect(retry.request.headers.get('Authorization')).toBe('Bearer NEW');
    retry.flush([]);
  });

  it('does not attempt refresh for login/refresh endpoints', () => {
    const auth = {
      getToken: () => 'AT',
      getRefreshToken: () => 'RT',
      refresh: () => of({ accessToken: 'NEW', refreshToken: 'RT2' }),
      logoutAndClear: () => of({}),
    } as Partial<AuthService>;

    setup(auth);

    http.post('/api/auth/auth/login', { a: 1 }).subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/auth/auth/login');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
    // should not retry
  });

  it('on 401 without refresh token: logout + navigate to /login', () => {
    const navigateByUrl = vi.fn().mockResolvedValue(true);

    const auth = {
      getToken: () => 'AT',
      getRefreshToken: () => null,
      logoutAndClear: () => of({}),
    } as Partial<AuthService>;

    setup(auth, { navigateByUrl });

    http.get('/api/site/admin/worksites').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/site/admin/worksites');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('if refresh fails: logout + navigate to /login', () => {
    const navigateByUrl = vi.fn().mockResolvedValue(true);

    const auth = {
      getToken: () => 'AT',
      getRefreshToken: () => 'RT',
      refresh: () => throwError(() => new Error('refresh failed')),
      logoutAndClear: () => of({}),
    } as Partial<AuthService>;

    setup(auth, { navigateByUrl });

    http.get('/api/site/admin/worksites').subscribe({ error: () => {} });

    const req = httpMock.expectOne('/api/site/admin/worksites');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(navigateByUrl).toHaveBeenCalledWith('/login');
  });
});

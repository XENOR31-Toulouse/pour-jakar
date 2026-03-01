import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  it('returns true when logged in', () => {
    const auth = { isLoggedIn: () => true, getToken: () => 't' } as unknown as AuthService;
    const router = { parseUrl: (u: string) => ({ url: u }) } as unknown as Router;

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    const result = TestBed.runInInjectionContext(() => authGuard());
    expect(result).toBeTrue();
  });

  it('redirects to /login when not logged in', () => {
    const auth = { isLoggedIn: () => false, getToken: () => null } as unknown as AuthService;
    const router = { parseUrl: (u: string) => ({ url: u }) } as unknown as Router;

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    const result: any = TestBed.runInInjectionContext(() => authGuard());
    expect(result.url).toBe('/login');
  });
});

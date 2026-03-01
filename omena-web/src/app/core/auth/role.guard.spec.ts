import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from './auth.service';

describe('roleGuard', () => {
  it('redirects to /login if not logged in', () => {
    const auth = { isLoggedIn: () => false, getRole: () => null } as unknown as AuthService;
    const router = { parseUrl: (u: string) => ({ url: u }) } as unknown as Router;

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    const guardFn = TestBed.runInInjectionContext(() => roleGuard(['ADMIN']));
    const result: any = guardFn();
    expect(result.url).toBe('/login');
  });

  it('returns true if role allowed', () => {
    const auth = { isLoggedIn: () => true, getRole: () => 'ADMIN' } as unknown as AuthService;
    const router = { parseUrl: (u: string) => ({ url: u }) } as unknown as Router;

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    const guardFn = TestBed.runInInjectionContext(() => roleGuard(['ADMIN', 'MANAGER']));
    expect(guardFn()).toBeTrue();
  });

  it('redirects to / if role not allowed', () => {
    const auth = { isLoggedIn: () => true, getRole: () => 'EMPLOYEE' } as unknown as AuthService;
    const router = { parseUrl: (u: string) => ({ url: u }) } as unknown as Router;

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    const guardFn = TestBed.runInInjectionContext(() => roleGuard(['ADMIN']));
    const result: any = guardFn();
    expect(result.url).toBe('/');
  });
});

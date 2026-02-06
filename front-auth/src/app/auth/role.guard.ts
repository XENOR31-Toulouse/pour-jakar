import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard = (allowed: string[]): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) return router.parseUrl('/login');

  const role = auth.getRole();
  if (role && allowed.includes(role)) return true;

  return router.parseUrl('/');
};

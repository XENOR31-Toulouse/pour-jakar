import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  console.log('[authGuard] isLoggedIn=', auth.isLoggedIn(), 'token=', auth.getToken());

  return auth.isLoggedIn() ? true : router.parseUrl('/login');
};

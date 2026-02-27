import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.getToken();
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) return throwError(() => err);

      // never refresh for auth endpoints
      if (req.url.includes('/api/auth/login') || req.url.includes('/api/auth/refresh')) {
        return throwError(() => err);
      }

      // if no refresh token -> go login
      if (!auth.getRefreshToken()) {
        auth.logoutAndClear().subscribe({ error: () => {} });
        router.navigateByUrl('/login');
        return throwError(() => err);
      }

      return auth.refresh().pipe(
        switchMap(() => {
          const newToken = auth.getToken();
          const retryReq = newToken
            ? req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
            : req;
          return next(retryReq);
        }),
        catchError(e => {
          // refresh failed -> clear + go login
          auth.logoutAndClear().subscribe({ error: () => {} });
          router.navigateByUrl('/login');
          return throwError(() => e);
        })
      );
    })
  );
};

import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  const token = localStorage.getItem('accessToken');
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status !== 401) return throwError(() => err);

      // évite boucle infinie : ne refresh pas sur /login ou /refresh
      if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
        return throwError(() => err);
      }

      return auth.refresh().pipe(
        switchMap(() => {
          const newToken = localStorage.getItem('accessToken');
          const retryReq = newToken
            ? req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
            : req;
          return next(retryReq);
        }),
        catchError(e => throwError(() => e))
      );
    })
  );
};

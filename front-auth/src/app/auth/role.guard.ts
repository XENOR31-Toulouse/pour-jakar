// import { CanActivateFn } from '@angular/router';
// import { inject } from '@angular/core';
// import { Router } from '@angular/router';
// import { AuthService } from './auth.service';

// export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
//   return () => {
//     const auth = inject(AuthService);
//     const router = inject(Router);

//     if (!auth.isLoggedIn()) return router.parseUrl('/login');

//     const role = auth.getRole();
//     if (role && allowedRoles.includes(role)) return true;

//     return router.parseUrl('/'); // ou /forbidden
//   };
// };

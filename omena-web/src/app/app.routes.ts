import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { authGuard } from './auth/auth.guard';
import { ProtectedComponent } from './protected/protected.component';
import { AdminCreateUserComponent } from './admin-create-user/admin-create-user.component';
import { roleGuard } from './auth/role.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'protected', component: ProtectedComponent, canActivate: [authGuard] },

  {
    path: 'admin/create-user',
    component: AdminCreateUserComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },

  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: '**', redirectTo: '' },
];

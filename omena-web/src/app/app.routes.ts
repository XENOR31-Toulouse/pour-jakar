import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { ProtectedComponent } from './features/home/protected.component';

import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegisterComponent } from './features/auth/pages/register/register.component';
import { ForgotPasswordComponent } from './features/auth/pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './features/auth/pages/reset-password/reset-password.component';

import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

import { AdminCreateUserComponent } from './features/admin/pages/create-user/admin-create-user.component';
import { AdminEmployeesComponent } from './features/admin/pages/employees/admin-employees.component';
import { AdminWorksitesComponent } from './features/admin/pages/worksites/admin-worksites.component';
import { AdminClientsComponent } from './features/admin/pages/clients/admin-clients.component';
import { AdminHomeComponent } from './features/admin/pages/admin-home.component';

import { WorksiteDetailComponent } from './features/worksites/pages/worksite-detail/worksite-detail.component';
import { MyWorksitesComponent } from './features/worksites/pages/my-worksites/user-worksite.component';
import { ClientsPageComponent } from './features/worksites/pages/clients/clients-page/clients-page.component';
import { ClientCreateComponent } from './features/worksites/pages/clients/client-create/client-create.component';
import { ClientEditComponent } from './features/worksites/pages/clients/client-edit/client-edit.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'protected', component: ProtectedComponent, canActivate: [authGuard] },
  {
    path: 'admin/worksites',
    component: AdminWorksitesComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  { path: 'worksites/:id', component: WorksiteDetailComponent, canActivate: [authGuard] },
  { path: 'my-worksites', component: MyWorksitesComponent, canActivate: [authGuard] },
  {
    path: 'admin/clients',
    component: ClientsPageComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/clients/create',
    component: ClientCreateComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/clients/:id/edit',
    component: ClientEditComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/clients-home',
    component: AdminClientsComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  {
    path: 'admin',
    component: AdminHomeComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },

  {
    path: 'admin/create-user',
    component: AdminCreateUserComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/employees',
    component: AdminEmployeesComponent,
    canActivate: [roleGuard(['ADMIN'])],
  },

  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: '**', redirectTo: '' },
];

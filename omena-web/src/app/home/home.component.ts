import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen p-6">
      <div class="max-w-xl mx-auto rounded-2xl shadow p-6 bg-white">
        <h1 class="text-2xl font-semibold mb-2">Front Auth</h1>

        <p class="mb-4">
          Connecté: <b>{{ auth.isLoggedIn() ? 'Oui' : 'Non' }}</b>
        </p>

        <div class="flex gap-2">
          <button class="rounded-xl p-3 border" (click)="goLogin()">Login</button>
          <button class="rounded-xl p-3 border" (click)="goRegister()">Register</button>
          <button class="rounded-xl p-3 border" (click)="logout()" [disabled]="!auth.isLoggedIn()">
            Logout
          </button>
          <button class="rounded-xl p-3 border" (click)="goForgot()">Forgot</button>
        </div>

        <div class="mt-4 text-xs break-all" *ngIf="auth.getToken()">
          <div class="font-semibold mb-1">Access token:</div>
          {{ auth.getToken() }}
        </div>

        <div class="mt-4 text-xs break-all" *ngIf="auth.getRefreshToken()">
          <div class="font-semibold mb-1">Refresh token:</div>
          {{ auth.getRefreshToken() }}
        </div>
        <button class="rounded-xl p-3 border" (click)="goProtected()">Protected</button>

        <button
          class="rounded-xl p-3 border"
          *ngIf="auth.getRole() === 'ADMIN'"
          (click)="goAdminEmployees()"
        >
          Admin: Employés
        </button>

        <div class="mt-4">
          <button
            class="rounded-xl p-3 border"
            *ngIf="auth.getRole() === 'ADMIN'"
            (click)="goAdminCreate()"
          >
            Admin: Create User
          </button>
        </div>
      </div>
    </div>
  `,
})
export class HomeComponent {
  constructor(
    public auth: AuthService,
    private router: Router,
  ) {}
  goLogin() {
    this.router.navigateByUrl('/login');
  }
  goRegister() {
    this.router.navigateByUrl('/register');
  }
logout() {
  this.auth.logoutAndClear().subscribe({
    next: () => this.router.navigateByUrl('/login'),
    error: () => this.router.navigateByUrl('/login'), // even if backend fails
  });
}
  goForgot() {
    this.router.navigateByUrl('/forgot-password');
  }
  goProtected() {
    this.router.navigateByUrl('/protected');
  }
  goAdminCreate() {
    this.router.navigateByUrl('/admin/create-user');
  }
  goAdminEmployees() {
    this.router.navigateByUrl('/admin/employees');
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen p-6">
      <div class="max-w-2xl mx-auto rounded-2xl shadow p-6 bg-white">
        <h1 class="text-2xl font-semibold mb-2">Omena App</h1>

        <p class="mb-2">
          Connecté: <b>{{ auth.isLoggedIn() ? 'Oui' : 'Non' }}</b>
        </p>

        <p class="mb-4" *ngIf="auth.isLoggedIn()">
          Rôle: <b>{{ auth.getRole() || 'USER' }}</b>
        </p>

        <!-- Auth actions -->
        <div class="flex flex-wrap gap-2 mb-6">
          <button class="rounded-xl p-3 border" (click)="goLogin()">Login</button>
          <button class="rounded-xl p-3 border" (click)="goRegister()">Register</button>
          <button class="rounded-xl p-3 border" (click)="logout()" [disabled]="!auth.isLoggedIn()">
            Logout
          </button>
          <button class="rounded-xl p-3 border" (click)="goForgot()">Forgot</button>
          <button class="rounded-xl p-3 border" (click)="goProtected()">Protected</button>
        </div>

        <!-- User features -->
        <div class="mb-6">
          <h2 class="font-semibold mb-2">Espace Employé</h2>
          <div class="flex flex-wrap gap-2">
            <button class="rounded-xl p-3 border" (click)="goMyWorksites()" [disabled]="!auth.isLoggedIn()">
              Mes chantiers
            </button>
          </div>
          <p class="text-xs opacity-70 mt-2">
            (Tu dois être connecté pour voir tes chantiers.)
          </p>
        </div>

        <!-- Admin features -->
        <div class="mb-6" *ngIf="auth.getRole() === 'ADMIN'">
          <h2 class="font-semibold mb-2">Espace Admin</h2>
          <div class="flex flex-wrap gap-2">
            <button class="rounded-xl p-3 border" (click)="goAdminEmployees()">
              Admin: Employés
            </button>

            <button class="rounded-xl p-3 border" (click)="goAdminWorksites()">
              Admin: Chantiers
            </button>

            <!-- si tu gardes cette page -->
            <button class="rounded-xl p-3 border" (click)="goAdminCreate()">
              Admin: Create User
            </button>
          </div>
        </div>

        <!-- Debug tokens -->
        <details class="mt-4" *ngIf="auth.getToken()">
          <summary class="cursor-pointer font-semibold">Debug tokens</summary>

          <div class="mt-3 text-xs break-all">
            <div class="font-semibold mb-1">Access token:</div>
            {{ auth.getToken() }}
          </div>

          <div class="mt-3 text-xs break-all" *ngIf="auth.getRefreshToken()">
            <div class="font-semibold mb-1">Refresh token:</div>
            {{ auth.getRefreshToken() }}
          </div>
        </details>
      </div>
    </div>
  `,
})
export class HomeComponent {
  constructor(public auth: AuthService, private router: Router) {}

  goLogin() { this.router.navigateByUrl('/login'); }
  goRegister() { this.router.navigateByUrl('/register'); }
  goForgot() { this.router.navigateByUrl('/forgot-password'); }
  goProtected() { this.router.navigateByUrl('/protected'); }

  goMyWorksites() { this.router.navigateByUrl('/my-worksites'); }

  goAdminEmployees() { this.router.navigateByUrl('/admin/employees'); }
  goAdminWorksites() { this.router.navigateByUrl('/admin/worksites'); }
  goAdminCreate() { this.router.navigateByUrl('/admin/create-user'); }

  logout() {
    this.auth.logoutAndClear().subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login'),
    });
  }
}

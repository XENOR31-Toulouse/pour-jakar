import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-md rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Connexion</h1>

      <div class="space-y-3">
        <input class="w-full border rounded-xl p-3" placeholder="Email ou pseudo"
          [(ngModel)]="identifier" />
        <input class="w-full border rounded-xl p-3" placeholder="Mot de passe" type="password"
          [(ngModel)]="password" />
        <button class="w-full rounded-xl p-3 border" (click)="onLogin()">
          Se connecter
        </button>

        <p *ngIf="error" class="text-sm text-red-600">{{ error }}</p>

        <a class="text-sm underline" routerLink="/register">Créer un compte</a>
      </div>
    </div>
  </div>
  `
})
export class LoginComponent {
  identifier = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  onLogin() {
    this.error = '';
    this.auth.login({ identifier: this.identifier, password: this.password })
      .subscribe({
        next: () => this.router.navigateByUrl('/'),
        error: (e) => this.error = e?.error?.message ?? 'Erreur login'
      });
  }
}

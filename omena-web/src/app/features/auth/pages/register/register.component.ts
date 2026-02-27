import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-md rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Créer un compte</h1>

      <div class="space-y-3">
        <input class="w-full border rounded-xl p-3" placeholder="Email"
          [(ngModel)]="email" />
        <input class="w-full border rounded-xl p-3" placeholder="Pseudo"
          [(ngModel)]="username" />
        <input class="w-full border rounded-xl p-3" placeholder="Mot de passe (8+)" type="password"
          [(ngModel)]="password" />

        <button class="w-full rounded-xl p-3 border" (click)="onRegister()">
          Créer
        </button>

        <p *ngIf="error" class="text-sm text-red-600">{{ error }}</p>

        <a class="text-sm underline" routerLink="/login">Retour login</a>
      </div>
    </div>
  </div>
  `
})
export class RegisterComponent {
  email = '';
  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  onRegister() {
    this.error = '';
    this.auth.register({ email: this.email, username: this.username, password: this.password })
      .subscribe({
        next: () => this.router.navigateByUrl('/login'),
        error: (e) => this.error = e?.error?.message ?? 'Erreur register'
      });
  }
}

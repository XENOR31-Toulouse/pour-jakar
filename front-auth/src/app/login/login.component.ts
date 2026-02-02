import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

type ApiError = { code?: string; message?: string; timestamp?: string };
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6">
      <div class="w-full max-w-md rounded-2xl shadow p-6 bg-white">
        <h1 class="text-2xl font-semibold mb-4">Connexion</h1>

        <div class="space-y-3">
          <input
            class="w-full border rounded-xl p-3"
            placeholder="Email ou pseudo"
            [(ngModel)]="identifier"
          />
          <input
            class="w-full border rounded-xl p-3"
            placeholder="Mot de passe"
            type="password"
            [(ngModel)]="password"
          />
          <button class="w-full rounded-xl p-3 border" (click)="onLogin()">Se connecter</button>

          <p *ngIf="error" class="text-sm text-red-600">{{ error }}</p>

          <a class="text-sm underline" routerLink="/register">Créer un compte</a>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  identifier = '';
  password = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onLogin() {
    this.error = '';
    this.cdr.markForCheck();

    this.auth.login({ identifier: this.identifier, password: this.password }).subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (e) => {
        const body = e?.error as ApiError | string | null | undefined;

        if (body && typeof body === 'object') {
          this.error = body.message ?? body.code ?? 'Identifiants invalides';
        } else if (typeof body === 'string' && body.trim()) {
          this.error = body;
        } else {
          this.error = (e?.status === 400 || e?.status === 401)
            ? 'Identifiants invalides'
            : 'Erreur serveur';
        }

        // 🔥 force l’UI à se mettre à jour immédiatement
        this.cdr.detectChanges();
      }
    });
  }
}

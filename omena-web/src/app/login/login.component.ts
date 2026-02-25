import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

type ApiError = { code?: string; message?: string; timestamp?: string };

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6 text-gray-900">
      <div class="w-full max-w-md bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">

        <div class="bg-blue-600 p-8 text-white text-center">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-white/20 rounded-full mb-4">
            <span class="text-3xl">👋</span>
          </div>
          <h1 class="text-2xl font-bold italic">Omena <span class="text-blue-200">App</span></h1>
          <p class="text-blue-100 text-sm mt-1">Connectez-vous pour gérer vos chantiers</p>
        </div>

        <div class="p-8 space-y-5">

          <div *ngIf="error" class="p-4 bg-red-50 border border-red-100 text-red-600 rounded-2xl text-xs font-bold animate-shake flex items-center gap-2">
            <span>⚠️</span> {{ error }}
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-bold text-gray-400 mb-1 ml-1 uppercase tracking-widest">Identifiant</label>
              <input
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
                placeholder="Email ou pseudo"
                [(ngModel)]="identifier"
                (keyup.enter)="onLogin()"
              />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-400 mb-1 ml-1 uppercase tracking-widest">Mot de passe</label>
              <input
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
                placeholder="••••••••"
                type="password"
                [(ngModel)]="password"
                (keyup.enter)="onLogin()"
              />
            </div>

            <button
              [disabled]="isLoading || !identifier || !password"
              class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white font-bold rounded-2xl p-4 shadow-lg shadow-blue-100 transition-all active:scale-95 flex items-center justify-center gap-2"
              (click)="onLogin()">
              <span *ngIf="isLoading" class="animate-spin border-2 border-white/30 border-t-white rounded-full h-4 w-4"></span>
              {{ isLoading ? 'Connexion...' : 'Se connecter' }}
            </button>
          </div>

          <div class="pt-6 border-t border-gray-50 flex flex-col items-center gap-3">
            <a class="text-xs font-bold text-blue-600 hover:underline" routerLink="/forgot-password">
              Mot de passe oublié ?
            </a>
            <p class="text-xs text-gray-400">
              Pas encore de compte ?
              <a class="text-blue-600 font-bold hover:underline ml-1" routerLink="/register">S'inscrire</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  identifier = '';
  password = '';
  error = '';
  isLoading = false;

  constructor(
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onLogin() {
    if (!this.identifier || !this.password) return;

    this.error = '';
    this.isLoading = true;
    this.cdr.markForCheck();

    this.auth.login({ identifier: this.identifier, password: this.password }).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigateByUrl('/');
      },
      error: (e) => {
        this.isLoading = false;
        const body = e?.error as ApiError | string | null | undefined;

        if (body && typeof body === 'object') {
          this.error = body.message ?? body.code ?? 'Identifiants invalides';
        } else if (typeof body === 'string' && body.trim()) {
          this.error = body;
        } else {
          this.error = (e?.status === 400 || e?.status === 401)
            ? 'Identifiants invalides'
            : 'Le serveur est injoignable';
        }

        // Force l’UI à se mettre à jour pour afficher l'erreur
        this.cdr.detectChanges();
      }
    });
  }
}

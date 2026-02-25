import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6 text-gray-900">
      <div class="w-full max-w-md bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">

        <div class="bg-blue-600 p-8 text-white text-center">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-white/20 rounded-full mb-4">
            <span class="text-3xl">🛡️</span>
          </div>
          <h1 class="text-2xl font-bold">Sécurisez votre compte</h1>
          <p class="text-blue-100 text-sm mt-2">Choisissez un nouveau mot de passe fort.</p>
        </div>

        <div class="p-8 space-y-6">

          <div *ngIf="!msg; else successTemplate">
            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 mb-1 ml-1 uppercase tracking-widest">Nouveau mot de passe</label>
                <input
                  class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm font-mono"
                  placeholder="••••••••"
                  type="password"
                  [(ngModel)]="newPassword"
                  (keyup.enter)="reset()" />
                <p class="text-[10px] text-gray-400 mt-2 ml-1 italic">Minimum 8 caractères conseillés.</p>
              </div>

              <button
                [disabled]="newPassword.length < 4 || isLoading"
                (click)="reset()"
                class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white font-bold rounded-2xl p-4 shadow-lg shadow-blue-100 transition-all active:scale-95 flex items-center justify-center gap-2">
                <span *ngIf="isLoading" class="animate-spin border-2 border-white/30 border-t-white rounded-full h-4 w-4"></span>
                {{ isLoading ? 'Mise à jour...' : 'Valider le changement' }}
              </button>
            </div>
          </div>

          <ng-template #successTemplate>
            <div class="text-center py-4 space-y-4 animate-bounce-short">
              <div class="text-green-500 text-5xl">✅</div>
              <p class="text-sm font-bold text-gray-800">
                {{ msg }}
              </p>
              <p class="text-xs text-gray-500 italic">Redirection vers la connexion...</p>
            </div>
          </ng-template>

          <div *ngIf="err" class="p-4 bg-red-50 border border-red-100 text-red-600 rounded-2xl text-[11px] font-bold text-center">
            ⚠️ {{ err }}
          </div>

          <div class="pt-4 border-t border-gray-50 text-center">
            <a routerLink="/login" class="text-xs font-bold text-gray-400 hover:text-blue-600 transition-colors uppercase tracking-widest">
              Annuler
            </a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ResetPasswordComponent {
  token = '';
  newPassword = '';
  msg = '';
  err = '';
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
  }

  reset() {
    if (!this.newPassword || this.isLoading) return;

    this.isLoading = true;
    this.msg = '';
    this.err = '';

    this.auth.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.isLoading = false;
        this.msg = "Mot de passe mis à jour avec succès !";
        // Redirection un peu plus lente pour laisser l'utilisateur lire le message de succès
        setTimeout(() => this.router.navigateByUrl('/login'), 2000);
      },
      error: (e) => {
        this.isLoading = false;
        // Si le token est expiré ou invalide, on donne un message clair
        if (e?.status === 400) {
          this.err = "Le lien est expiré ou invalide. Veuillez refaire une demande.";
        } else {
          this.err = e?.error?.message ?? 'Une erreur technique est survenue.';
        }
      }
    });
  }
}

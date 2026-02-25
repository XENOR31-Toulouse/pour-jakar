import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router'; // Importé pour le lien de retour
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6 text-gray-900">
      <div class="w-full max-w-md bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">

        <div class="bg-blue-600 p-8 text-white text-center">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-white/20 rounded-full mb-4">
            <span class="text-3xl">🔑</span>
          </div>
          <h1 class="text-2xl font-bold">Mot de passe oublié</h1>
          <p class="text-blue-100 text-sm mt-2">Pas d'inquiétude, nous allons vous aider.</p>
        </div>

        <div class="p-8 space-y-6">

          <div *ngIf="!msg; else successTemplate">
            <p class="text-sm text-gray-500 mb-6 text-center">
              Entrez votre adresse email pour recevoir un lien de réinitialisation sécurisé.
            </p>

            <div class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-400 mb-1 ml-1 uppercase tracking-widest">Votre Email</label>
                <input
                  class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
                  placeholder="nom@entreprise.fr"
                  [(ngModel)]="email"
                  type="email" />
              </div>

              <button
                [disabled]="!email || isLoading"
                (click)="send()"
                class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white font-bold rounded-2xl p-4 shadow-lg shadow-blue-100 transition-all active:scale-95 flex items-center justify-center gap-2">
                <span *ngIf="isLoading" class="animate-spin border-2 border-white/30 border-t-white rounded-full h-4 w-4"></span>
                {{ isLoading ? 'Envoi en cours...' : 'Envoyer le lien' }}
              </button>
            </div>
          </div>

          <ng-template #successTemplate>
            <div class="text-center py-4 space-y-4">
              <div class="text-green-500 text-5xl">📧</div>
              <p class="text-sm font-medium text-gray-700 leading-relaxed px-4">
                {{ msg }}
              </p>
              <button (click)="msg = ''" class="text-xs text-blue-600 font-bold uppercase hover:underline">
                Renvoyer un mail
              </button>
            </div>
          </ng-template>

          <div *ngIf="err" class="p-4 bg-red-50 border border-red-100 text-red-600 rounded-2xl text-xs font-bold animate-shake">
            ⚠️ {{ err }}
          </div>

          <div class="pt-4 border-t border-gray-50 flex justify-center">
            <a routerLink="/login" class="text-sm font-bold text-gray-400 hover:text-blue-600 transition-colors flex items-center gap-2">
              ⬅️ Retour à la connexion
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ForgotPasswordComponent {
  email = '';
  msg = '';
  err = '';
  isLoading = false;

  constructor(private auth: AuthService) {}

  send() {
    if (!this.email) return;

    this.isLoading = true;
    this.msg = '';
    this.err = '';

    this.auth.requestPasswordReset(this.email).subscribe({
      next: () => {
        this.isLoading = false;
        this.msg = "Si l'adresse correspond à un compte, vous recevrez un email de réinitialisation d'ici quelques instants.";
      },
      error: (e) => {
        this.isLoading = false;
        this.err = e?.error?.message ?? "Une erreur est survenue. Veuillez vérifier votre connexion.";
      }
    });
  }
}

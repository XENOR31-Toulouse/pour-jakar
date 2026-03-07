import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6 text-gray-900">
      <div class="w-full max-w-lg bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">

        <div class="bg-slate-900 p-8 text-white">
          <h1 class="text-2xl font-bold flex items-center gap-3">
            <span class="bg-blue-600 p-2 rounded-lg text-xl">👤</span>
            Créer un utilisateur
          </h1>
          <p class="text-slate-400 text-sm mt-2 font-medium uppercase tracking-wider">Interface Administration</p>
        </div>

        <div class="p-8 space-y-5">

          <div *ngIf="msg" class="p-4 bg-green-50 border border-green-100 text-green-700 rounded-2xl text-sm font-medium animate-pulse">
            ✅ {{ msg }}
          </div>
          <div *ngIf="err" class="p-4 bg-red-50 border border-red-100 text-red-600 rounded-2xl text-sm font-medium">
            ⚠️ {{ err }}
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-bold text-gray-500 mb-1 ml-1 uppercase tracking-widest">Adresse Email</label>
              <input
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
                placeholder="exemple@omena.fr"
                [(ngModel)]="email" />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-500 mb-1 ml-1 uppercase tracking-widest">Nom d'utilisateur</label>
              <input
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
                placeholder="JeanDupont"
                [(ngModel)]="username" />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-500 mb-1 ml-1 uppercase tracking-widest">Mot de passe temporaire</label>
              <input
                class="w-full bg-gray-50 border border-gray-200 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm font-mono"
                type="password"
                placeholder="••••••••"
                [(ngModel)]="password" />
              <p class="text-[10px] text-gray-400 mt-2 ml-1 italic">Minimum 8 caractères conseillés.</p>
            </div>

            <div class="flex items-center gap-3 p-3">
              <input type="checkbox" id="is-admin" class="h-5 w-5 rounded-md" [(ngModel)]="isAdmin" />
              <label for="is-admin" class="text-sm font-bold text-gray-600">Attribuer les privilèges ADMIN ?</label>
            </div>
          </div>

          <div class="pt-4 space-y-3">
            <button
              (click)="create()"
              [disabled]="!email || !username || !password"
              class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white font-bold rounded-2xl p-4 shadow-lg shadow-blue-200 transition-all active:scale-95">
              Créer le compte
            </button>

            <a routerLink="/"
               class="flex items-center justify-center gap-2 text-sm font-bold text-gray-400 hover:text-gray-600 transition-colors py-2">
              <span>⬅️</span> Retour au tableau de bord
            </a>
          </div>
        </div>

      </div>
    </div>
  `
})
export class AdminCreateUserComponent {
  email = '';
  username = '';
  password = '';
  isAdmin = false;
  msg = '';
  err = '';

  constructor(private auth: AuthService) {}

  create() {
    this.msg = '';
    this.err = '';

    // Une petite vérification côté client avant d'envoyer
    if (this.password.length < 4) {
      this.err = 'Le mot de passe est trop court';
      return;
    }

    this.auth.adminCreateUser(this.email, this.username, this.password, this.isAdmin).subscribe({
      next: (res) => {
        this.msg = `L'utilisateur ${this.username} a été créé avec succès (ID: ${res.userId})`;
        // Reset des champs après succès
        this.email = '';
        this.username = '';
        this.password = '';
        this.isAdmin = false;
      },
      error: (e) => {
        const status = e?.status;
        if (status === 409) this.err = e?.error?.message ?? 'Email ou pseudo déjà utilisé';
        else if (status === 400) this.err = e?.error?.message ?? 'Format des données invalide';
        else if (status === 403) this.err = 'Action interdite (Privilèges ADMIN requis)';
        else this.err = 'Une erreur serveur est survenue lors de la création';
      }
    });
  }
}

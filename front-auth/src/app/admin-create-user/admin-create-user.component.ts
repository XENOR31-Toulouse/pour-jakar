import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
  <div class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-lg rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Admin — Créer un utilisateur</h1>

      <div class="space-y-3">
        <input class="w-full border rounded-xl p-3" placeholder="Email" [(ngModel)]="email" />
        <input class="w-full border rounded-xl p-3" placeholder="Pseudo" [(ngModel)]="username" />
        <input class="w-full border rounded-xl p-3" placeholder="Mot de passe (8+)" type="password" [(ngModel)]="password" />

        <button class="w-full rounded-xl p-3 border" (click)="create()">Créer</button>

        <p *ngIf="msg" class="text-sm text-green-700">{{ msg }}</p>
        <p *ngIf="err" class="text-sm text-red-600">{{ err }}</p>

        <a class="text-sm underline" routerLink="/">Retour</a>
      </div>
    </div>
  </div>
  `
})
export class AdminCreateUserComponent {
  email = '';
  username = '';
  password = '';
  msg = '';
  err = '';

  constructor(private auth: AuthService) {}

  create() {
    this.msg = ''; this.err = '';
    this.auth.adminCreateUser(this.email, this.username, this.password).subscribe({
      next: (res) => this.msg = `Utilisateur créé: ${res.userId}`,
      error: (e) => {
        const status = e?.status;
        if (status === 409) this.err = e?.error?.message ?? 'Email/pseudo déjà utilisé';
        else if (status === 400) this.err = e?.error?.message ?? 'Champs invalides';
        else if (status === 403) this.err = 'Accès refusé (ADMIN requis)';
        else this.err = 'Erreur serveur';
      }
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-md rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Mot de passe oublié</h1>

      <input class="w-full border rounded-xl p-3" placeholder="Email" [(ngModel)]="email" />
      <button class="w-full rounded-xl p-3 border mt-3" (click)="send()">Envoyer</button>

      <p class="text-sm mt-3" *ngIf="msg">{{ msg }}</p>
      <p class="text-sm text-red-600 mt-3" *ngIf="err">{{ err }}</p>
    </div>
  </div>
  `
})
export class ForgotPasswordComponent {
  email = '';
  msg = '';
  err = '';

  constructor(private auth: AuthService) {}

  send() {
    this.msg = ''; this.err = '';
    this.auth.requestPasswordReset(this.email).subscribe({
      next: () => this.msg = "Si l'email existe, un lien de reset a été envoyé.",
      error: (e) => this.err = e?.error?.message ?? 'Erreur'
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-md rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Réinitialiser le mot de passe</h1>

      <input class="w-full border rounded-xl p-3" placeholder="Nouveau mot de passe (8+)" type="password"
        [(ngModel)]="newPassword" />

      <button class="w-full rounded-xl p-3 border mt-3" (click)="reset()">Valider</button>

      <p class="text-sm mt-3" *ngIf="msg">{{ msg }}</p>
      <p class="text-sm text-red-600 mt-3" *ngIf="err">{{ err }}</p>
    </div>
  </div>
  `
})
export class ResetPasswordComponent {
  token = '';
  newPassword = '';
  msg = '';
  err = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
  }

  reset() {
    this.msg = ''; this.err = '';
    this.auth.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.msg = "Mot de passe mis à jour. Tu peux te connecter.";
        setTimeout(() => this.router.navigateByUrl('/login'), 800);
      },
      error: (e) => this.err = e?.error?.message ?? 'Erreur'
    });
  }
}

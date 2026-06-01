import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-4 md:p-8 text-gray-900">
      <div class="max-w-4xl mx-auto">

        <div class="bg-white rounded-3xl shadow-sm p-6 mb-8 flex flex-col md:flex-row justify-between items-center gap-4 border border-gray-100">
          <div>
            <h1 class="text-3xl font-extrabold tracking-tight italic">Omena <span class="text-blue-600">App</span></h1>
            <p class="text-gray-500 mt-1" *ngIf="auth.isLoggedIn()">Ravi de vous revoir !</p>
          </div>

          <div class="flex items-center gap-3 bg-gray-50 px-4 py-2 rounded-2xl border border-gray-100">
            <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
              {{ auth.getRole() === 'ADMIN' ? 'A' : 'U' }}
            </div>
            <div>
              <div class="text-sm font-bold">{{ auth.isLoggedIn() ? 'Session Active' : 'Non connecté' }}</div>
              <div class="text-[10px] text-blue-600 font-bold uppercase tracking-widest">{{ auth.getRole() || 'Invité' }}</div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">

          <div class="space-y-6">
            <section>
              <h2 class="text-lg font-bold mb-4 px-2">Compte & Sécurité</h2>
              <div class="grid grid-cols-2 gap-3">
                <button *ngIf="!auth.isLoggedIn()" (click)="goLogin()"
                        class="bg-white border border-gray-100 flex flex-col items-center justify-center p-4 rounded-2xl shadow-sm transition-all hover:shadow-md active:scale-95 hover:bg-blue-50 group">
                  <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">🔑</span>
                  <span class="font-semibold text-sm">Login</span>
                </button>

                <button *ngIf="!auth.isLoggedIn()" (click)="goRegister()"
                        class="bg-white border border-gray-100 flex flex-col items-center justify-center p-4 rounded-2xl shadow-sm transition-all hover:shadow-md active:scale-95 hover:bg-gray-50 group">
                  <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">📝</span>
                  <span class="font-semibold text-sm">Register</span>
                </button>

                <button (click)="goForgot()"
                        class="bg-white border border-gray-100 flex flex-col items-center justify-center p-4 rounded-2xl shadow-sm transition-all hover:shadow-md active:scale-95 hover:bg-gray-50 group">
                  <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">❓</span>
                  <span class="font-semibold text-sm text-gray-600">Oubli</span>
                </button>

                <button *ngIf="auth.isLoggedIn()" (click)="logout()"
                        class="bg-white border border-red-50 flex flex-col items-center justify-center p-4 rounded-2xl shadow-sm transition-all hover:shadow-md active:scale-95 hover:bg-red-50 group">
                  <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">🚪</span>
                  <span class="font-semibold text-sm text-red-600">Déconnexion</span>
                </button>
              </div>
            </section>

            <section>
              <h2 class="text-lg font-bold mb-4 px-2">Travail</h2>
              <button (click)="goMyWorksites()"
                      [disabled]="!auth.isLoggedIn()"
                      class="w-full flex items-center justify-between p-5 rounded-2xl border bg-white transition-all shadow-sm hover:shadow-md disabled:opacity-50 disabled:grayscale group">
                <div class="flex items-center gap-4">
                  <div class="bg-blue-600 p-3 rounded-xl text-white group-hover:rotate-12 transition-transform shadow-lg shadow-blue-200">🏗️</div>
                  <div class="text-left">
                    <div class="font-bold text-gray-900">Mes chantiers</div>
                    <div class="text-xs text-gray-500">Pointer et voir l'avancement</div>
                  </div>
                </div>
                <span class="text-gray-300 group-hover:translate-x-1 transition-transform">➡️</span>
              </button>
              <p *ngIf="!auth.isLoggedIn()" class="text-[11px] text-amber-600 mt-3 px-2 flex items-center gap-1 font-medium">
                ⚠️ Connectez-vous pour voir vos chantiers.
              </p>
            </section>
          </div>

          <div class="space-y-6">
            <section *ngIf="auth.getRole() === 'ADMIN'">
              <h2 class="text-lg font-bold mb-4 px-2 text-blue-700">Console Admin</h2>
              <div class="bg-slate-900 rounded-3xl p-6 text-white shadow-2xl space-y-2 border border-slate-800">
                <button (click)="goAdminEmployees()" class="w-full flex justify-between items-center p-3 rounded-xl hover:bg-white/10 transition-colors font-medium border-b border-white/5">
                  <span>👥 Gestion Employés</span>
                  <span class="text-slate-500">→</span>
                </button>
                <button (click)="goAdminWorksites()" class="w-full flex justify-between items-center p-3 rounded-xl hover:bg-white/10 transition-colors font-medium border-b border-white/5">
                  <span>🏗️ Gestion Chantiers</span>
                  <span class="text-slate-500">→</span>
                </button>
                <button (click)="goAdminCreate()" class="w-full flex justify-between items-center p-3 rounded-xl hover:bg-white/10 transition-colors font-medium">
                  <span>➕ Créer Utilisateur</span>
                  <span class="text-slate-500">→</span>
                </button>
                <button (click)="goAdminClients()" class="w-full flex justify-between items-center p-3 rounded-xl hover:bg-white/10 transition-colors font-medium">
                  <span>👔 Gestion Clients</span>
                  <span class="text-slate-500">→</span>
                </button>
              </div>
            </section>

            <details class="group bg-white border border-gray-100 rounded-2xl p-4 shadow-sm" *ngIf="auth.getToken()">
              <summary class="cursor-pointer font-bold text-gray-500 text-xs uppercase tracking-widest list-none flex justify-between items-center">
                <span>Outils Développeur</span>
                <span class="group-open:rotate-180 transition-transform">▼</span>
              </summary>
              <div class="mt-4 space-y-3 overflow-hidden text-[10px] font-mono bg-gray-50 p-4 rounded-xl border border-gray-100">
                <div class="break-all">
                  <span class="text-blue-600 font-bold uppercase">Token Accès:</span><br>
                  <div class="mt-1 text-gray-400 select-all">{{ auth.getToken() }}</div>
                </div>
                <div class="break-all border-t border-gray-200 pt-3" *ngIf="auth.getRefreshToken()">
                  <span class="text-green-600 font-bold uppercase">Token Refresh:</span><br>
                  <div class="mt-1 text-gray-400 select-all">{{ auth.getRefreshToken() }}</div>
                </div>
              </div>
            </details>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class HomeComponent {
  constructor(public auth: AuthService, private router: Router) {}

  goLogin() { this.router.navigateByUrl('/login'); }
  goRegister() { this.router.navigateByUrl('/register'); }
  goForgot() { this.router.navigateByUrl('/forgot-password'); }
  goProtected() { this.router.navigateByUrl('/protected'); }
  goMyWorksites() { this.router.navigateByUrl('/my-worksites'); }
  goAdminEmployees() { this.router.navigateByUrl('/admin/employees'); }
  goAdminWorksites() { this.router.navigateByUrl('/admin/worksites'); }
  goAdminCreate() { this.router.navigateByUrl('/admin/create-user'); }
  goAdminClients() { this.router.navigateByUrl('/admin/clients'); }

  logout() {
    this.auth.logoutAndClear().subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login'),
    });
  }
}

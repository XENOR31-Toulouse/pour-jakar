import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

type EmployeeDto = { id: string; email: string; username: string; createdAt: string };

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-4 md:p-8 text-gray-900">
      <div class="max-w-5xl mx-auto">

        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
          <div>
            <h1 class="text-3xl font-extrabold flex items-center gap-3">
              <span class="bg-blue-600 p-2 rounded-xl text-white shadow-lg shadow-blue-200">👥</span>
              Gestion des Employés
            </h1>
            <p class="text-gray-500 mt-1 font-medium italic">Consultez, créez et gérez les accès de vos collaborateurs.</p>
          </div>
          <div class="flex gap-2">
            <button (click)="load()"
                    [disabled]="isLoading"
                    class="bg-white border border-gray-200 px-4 py-2 rounded-xl text-sm font-bold shadow-sm hover:bg-gray-50 transition-all active:scale-95 disabled:opacity-50">
              <span [class.animate-spin]="isLoading" class="inline-block">🔄</span> Rafraîchir
            </button>
            <a routerLink="/" class="bg-white border border-gray-200 px-4 py-2 rounded-xl text-sm font-bold shadow-sm hover:bg-gray-50 transition-all flex items-center gap-2">
              ⬅️ Retour
            </a>
          </div>
        </div>

        <div class="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-8">
          <h2 class="text-sm font-bold text-gray-400 uppercase tracking-widest mb-4">Ajout rapide</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <input class="w-full bg-gray-50 border border-gray-100 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
                   placeholder="Email professionnel" [(ngModel)]="email" />
            <input class="w-full bg-gray-50 border border-gray-100 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
                   placeholder="Nom d'utilisateur" [(ngModel)]="username" />
            <div class="flex gap-2">
              <input class="flex-1 bg-gray-50 border border-gray-100 rounded-2xl p-4 outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
                     placeholder="Mot de passe" type="password" [(ngModel)]="password" />
              <button (click)="create()"
                      [disabled]="isLoading"
                      class="bg-blue-600 text-white font-bold px-6 rounded-2xl hover:bg-blue-700 transition-colors shadow-lg shadow-blue-100 disabled:opacity-50">
                Créer
              </button>
            </div>
          </div>
          <div *ngIf="msg" class="mt-4 p-3 bg-green-50 border border-green-100 text-green-700 rounded-xl text-xs font-bold animate-fade-in">✅ {{ msg }}</div>
          <div *ngIf="err" class="mt-4 p-3 bg-red-50 border border-red-100 text-red-600 rounded-xl text-xs font-bold animate-fade-in">⚠️ {{ err }}</div>
        </div>

        <div class="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="bg-slate-900 text-white uppercase text-[10px] tracking-widest font-bold">
                  <th class="p-5">Collaborateur</th>
                  <th class="p-5">Identifiant Unique (ID)</th>
                  <th class="p-5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                <tr *ngFor="let e of employees" class="group hover:bg-blue-50/30 transition-colors">
                  <td class="p-5">
                    <div class="flex items-center gap-3">
                      <div class="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center font-bold text-gray-400 group-hover:bg-blue-100 group-hover:text-blue-600 transition-colors uppercase">
                        {{ e.username.charAt(0) }}
                      </div>
                      <div>
                        <div class="font-bold text-gray-800 text-sm">{{ e.username }}</div>
                        <div class="text-xs text-gray-400">{{ e.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="p-5">
                    <code class="text-[10px] bg-gray-100 px-2 py-1 rounded-md text-gray-500 font-mono">{{ e.id }}</code>
                  </td>
                  <td class="p-5 text-right">
                    <button (click)="remove(e.id)"
                            [disabled]="isLoading"
                            class="text-red-400 hover:text-red-600 hover:bg-red-50 px-4 py-2 rounded-xl transition-all font-bold text-xs disabled:opacity-30">
                      Supprimer
                    </button>
                  </td>
                </tr>

                <tr *ngIf="!isLoading && employees.length === 0">
                  <td colspan="3" class="p-10 text-center text-gray-400 italic text-sm">
                    Aucun collaborateur trouvé.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  `
})
export class AdminEmployeesComponent implements OnInit {
  employees: EmployeeDto[] = [];
  email = '';
  username = '';
  password = '';
  msg = '';
  err = '';
  isLoading = false;

  constructor(
    private auth: AuthService,
    private cdr: ChangeDetectorRef // 🔥 Injecté pour forcer le rafraîchissement
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.isLoading = true;
    this.auth.adminListEmployees().subscribe({
      next: (res) => {
        // 🔥 Création d'une nouvelle référence + Tri alphabétique par pseudo
        this.employees = [...(res ?? [])].sort((a, b) =>
          a.username.localeCompare(b.username)
        );
        this.isLoading = false;
        this.cdr.detectChanges(); // 🔥 Force Angular à mettre à jour la vue
      },
      error: (e) => {
        this.isLoading = false;
        this.err = e?.status === 403 ? 'Droits administrateur requis' : 'Échec du chargement';
        this.cdr.detectChanges();
      }
    });
  }

  create() {
    if (!this.email || !this.username || !this.password) {
      this.err = 'Veuillez remplir tous les champs.';
      return;
    }

    this.isLoading = true;
    this.msg = '';
    this.err = '';

    this.auth.adminCreateEmployee(this.email, this.username, this.password).subscribe({
      next: () => {
        this.msg = `Le collaborateur ${this.username} a été ajouté.`;
        this.email = '';
        this.username = '';
        this.password = '';
        // 🔥 Rechargement immédiat
        this.load();
        this.clearMsg();
      },
      error: (e) => {
        this.isLoading = false;
        if (e?.status === 409) this.err = 'Cet email ou pseudo est déjà utilisé.';
        else this.err = 'Erreur lors de la création.';
        this.cdr.detectChanges();
      }
    });
  }

  remove(id: string) {
    if (!confirm('Voulez-vous vraiment révoquer l\'accès ?')) return;

    this.isLoading = true;
    this.msg = '';
    this.err = '';

    this.auth.adminDeleteEmployee(id).subscribe({
      next: () => {
        this.msg = 'Accès révoqué avec succès.';
        // 🔥 Rechargement immédiat
        this.load();
        this.clearMsg();
      },
      error: (e) => {
        this.isLoading = false;
        this.err = 'Échec de la suppression.';
        this.cdr.detectChanges();
      }
    });
  }

  private clearMsg() {
    setTimeout(() => {
      this.msg = '';
      this.err = '';
      this.cdr.detectChanges();
    }, 3000);
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

type EmployeeDto = { id: string; email: string; username: string; createdAt: string };

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
  <div class="min-h-screen p-6">
    <div class="max-w-3xl mx-auto rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Admin — Employés</h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-2 mb-4">
        <input class="border rounded-xl p-3" placeholder="Email" [(ngModel)]="email" />
        <input class="border rounded-xl p-3" placeholder="Pseudo" [(ngModel)]="username" />
        <input class="border rounded-xl p-3" placeholder="Mot de passe (8+)" type="password" [(ngModel)]="password" />
      </div>

      <div class="flex gap-2 mb-4">
        <button class="rounded-xl p-3 border" (click)="create()">Créer employé</button>
        <button class="rounded-xl p-3 border" (click)="load()">Rafraîchir</button>
        <a class="rounded-xl p-3 border inline-block" routerLink="/">Retour</a>
      </div>

      <p *ngIf="msg" class="text-sm text-green-700 mb-3">{{ msg }}</p>
      <p *ngIf="err" class="text-sm text-red-600 mb-3">{{ err }}</p>

      <div class="border rounded-xl overflow-hidden">
        <div class="grid grid-cols-12 gap-2 p-3 font-semibold border-b bg-gray-50">
          <div class="col-span-4">Email</div>
          <div class="col-span-3">Pseudo</div>
          <div class="col-span-4">Id</div>
          <div class="col-span-1 text-right">X</div>
        </div>

        <div *ngFor="let e of employees" class="grid grid-cols-12 gap-2 p-3 border-b items-center">
          <div class="col-span-4 truncate">{{ e.email }}</div>
          <div class="col-span-3 truncate">{{ e.username }}</div>
          <div class="col-span-4 text-xs break-all">{{ e.id }}</div>
          <div class="col-span-1 text-right">
            <button class="border rounded-lg px-2 py-1" (click)="remove(e.id)">Del</button>
          </div>
        </div>

        <div *ngIf="employees.length === 0" class="p-3 text-sm opacity-70">
          Aucun employé.
        </div>
      </div>
    </div>
  </div>
  `
})
export class AdminEmployeesComponent {
  employees: EmployeeDto[] = [];
  email = '';
  username = '';
  password = '';
  msg = '';
  err = '';

  constructor(private auth: AuthService) {}

  ngOnInit() { this.load(); }

  load() {
    this.msg = ''; this.err = '';
    this.auth.adminListEmployees().subscribe({
      next: (res) => this.employees = res,
      error: (e) => this.err = e?.status === 403 ? 'ADMIN requis' : 'Erreur chargement'
    });
  }

  create() {
    this.msg = ''; this.err = '';
    this.auth.adminCreateEmployee(this.email, this.username, this.password).subscribe({
      next: (res) => {
        this.msg = `Employé créé: ${res.userId}`;
        this.email = ''; this.username = ''; this.password = '';
        this.load();
      },
      error: (e) => {
        if (e?.status === 409) this.err = e?.error?.message ?? 'Email/pseudo déjà utilisé';
        else if (e?.status === 400) this.err = e?.error?.message ?? 'Champs invalides';
        else if (e?.status === 403) this.err = 'ADMIN requis';
        else this.err = 'Erreur serveur';
      }
    });
  }

  remove(id: string) {
    this.msg = ''; this.err = '';
    this.auth.adminDeleteEmployee(id).subscribe({
      next: () => { this.msg = 'Employé supprimé'; this.load(); },
      error: (e) => this.err = e?.status === 403 ? 'ADMIN requis' : 'Erreur suppression'
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { WorksiteService, Worksite, Assignment } from '../worksite/worksite.service';
import { AuthService } from '../auth/auth.service';
import { TimelineItem } from '../worksite/worksite.service';

type EmployeeDto = { id: string; email: string; username: string; createdAt: string };

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen p-6">
      <div class="max-w-5xl mx-auto rounded-2xl shadow p-6 bg-white">
        <h1 class="text-2xl font-semibold mb-4">Admin — Chantiers</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-2 mb-4">
          <input class="border rounded-xl p-3" placeholder="Nom chantier" [(ngModel)]="name" />
          <input class="border rounded-xl p-3" placeholder="Adresse" [(ngModel)]="address" />
          <button class="border rounded-xl p-3" (click)="create()">Créer chantier</button>
        </div>

        <div class="flex gap-2 mb-4">
          <button class="rounded-xl p-3 border" (click)="load()">Rafraîchir</button>
          <a class="rounded-xl p-3 border inline-block" routerLink="/">Retour</a>
        </div>

        <p *ngIf="msg" class="text-sm text-green-700 mb-3">{{ msg }}</p>
        <p *ngIf="err" class="text-sm text-red-600 mb-3">{{ err }}</p>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Worksites -->
          <div class="border rounded-xl p-4">
            <h2 class="font-semibold mb-2">Chantiers</h2>

            <div *ngFor="let w of worksites" class="border rounded-xl p-3 mb-2">
              <div class="font-semibold">{{ w.name }}</div>
              <div class="text-sm opacity-70">{{ w.address || '-' }}</div>
              <div class="text-xs break-all mt-1">id: {{ w.id }}</div>

              <div class="flex gap-2 mt-2">
                <button class="border rounded-lg px-2 py-1" (click)="selectWorksite(w)">
                  {{ selectedWorksite?.id === w.id ? 'Sélectionné' : 'Sélectionner' }}
                </button>
                <button class="border rounded-lg px-2 py-1" (click)="delWorksite(w.id)">
                  Supprimer
                </button>
              </div>
            </div>
          </div>

          <!-- Assigned employees -->
          <div class="border rounded-xl p-4">
            <h2 class="font-semibold mb-2">Employés assignés</h2>

            <div class="text-sm mb-3" *ngIf="selectedWorksite">
              Chantier: <b>{{ selectedWorksite.name }}</b>
            </div>

            <div *ngIf="!selectedWorksite" class="text-sm opacity-70">
              Sélectionne un chantier pour voir les affectations.
            </div>

            <div *ngIf="selectedWorksite && assignments.length === 0" class="text-sm opacity-70">
              Aucun employé assigné.
            </div>

            <div *ngFor="let a of assignments" class="border rounded-xl p-3 mb-2">
              <div class="font-semibold">
                {{ employeeName(a.userId) }}
              </div>
              <div class="text-sm opacity-70">
                {{ employeeEmail(a.userId) }}
              </div>
              <div class="text-xs opacity-70">Assigné le: {{ a.assignedAt | date: 'medium' }}</div>
              <div class="text-xs break-all mt-1">userId: {{ a.userId }}</div>

              <button class="border rounded-lg px-2 py-1 mt-2" (click)="unassign(a.userId)">
                Retirer
              </button>
            </div>
          </div>

          <div class="border rounded-xl p-4">
            <h2 class="font-semibold mb-2">Avancement / Timeline (Admin)</h2>

            <div *ngIf="!selectedWorksite" class="text-sm opacity-70">Sélectionne un chantier.</div>

            <div
              *ngIf="selectedWorksite && adminTimelineItems.length === 0"
              class="text-sm opacity-70"
            >
              Aucun événement.
            </div>

            <div *ngFor="let it of adminTimelineItems" class="border rounded-xl p-3 mb-2">
              <div class="font-semibold">
                {{ it.kind }}
                <span class="text-sm opacity-70">— {{ it.at | date: 'medium' }}</span>
              </div>

              <div class="text-sm opacity-70">
                {{ employeeName(it.userId) }} — {{ employeeEmail(it.userId) }}
              </div>

              <div class="text-sm mt-1" *ngIf="it.kind === 'PROGRESS'">
                {{ it.note }}
                <span *ngIf="it.percent !== null && it.percent !== undefined">
                  ({{ it.percent }}%)</span
                >
              </div>

              <div class="text-xs break-all mt-1">userId: {{ it.userId }}</div>
            </div>
          </div>

          <!-- Assign employees -->
          <div class="border rounded-xl p-4">
            <h2 class="font-semibold mb-2">Assigner un employé</h2>

            <div *ngIf="!selectedWorksite" class="text-sm opacity-70 mb-2">
              Sélectionne un chantier d’abord.
            </div>

            <div *ngFor="let e of employees" class="border rounded-xl p-3 mb-2">
              <div class="font-semibold">{{ e.username }}</div>
              <div class="text-sm opacity-70">{{ e.email }}</div>
              <div class="text-xs break-all mt-1">id: {{ e.id }}</div>

              <button
                class="border rounded-lg px-2 py-1 mt-2"
                [disabled]="!selectedWorksite || isAssigned(e.id)"
                (click)="assign(e.id)"
              >
                {{ isAssigned(e.id) ? 'Déjà assigné' : 'Assigner' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AdminWorksitesComponent {
  worksites: Worksite[] = [];
  employees: EmployeeDto[] = [];
  adminTimelineItems: TimelineItem[] = [];
  employeesById = new Map<string, EmployeeDto>();

  selectedWorksite: Worksite | null = null;
  assignments: Assignment[] = [];

  name = '';
  address = '';
  msg = '';
  err = '';

  constructor(
    private ws: WorksiteService,
    private auth: AuthService,
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.msg = '';
    this.err = '';

    this.ws.adminListWorksites().subscribe({
      next: (res) => (this.worksites = res),
      error: () => (this.err = 'Erreur chargement chantiers'),
    });

    this.auth.adminListEmployees().subscribe({
      next: (res: any) => {
        this.employees = res;
        this.employeesById = new Map(this.employees.map((e) => [e.id, e]));
      },
      error: () => (this.err = 'Erreur chargement employés'),
    });

    // si un chantier est déjà sélectionné, refresh ses affectations
    if (this.selectedWorksite) {
      this.refreshAssignments();
    }
  }

  create() {
    this.msg = '';
    this.err = '';
    this.ws.adminCreateWorksite(this.name, this.address).subscribe({
      next: (res) => {
        this.msg = `Chantier créé: ${res.id}`;
        this.name = '';
        this.address = '';
        this.load();
      },
      error: (e) => (this.err = e?.status === 403 ? 'ADMIN requis' : 'Erreur création'),
    });
  }

  delWorksite(id: string) {
    this.msg = '';
    this.err = '';
    this.ws.adminDeleteWorksite(id).subscribe({
      next: () => {
        if (this.selectedWorksite?.id === id) {
          this.selectedWorksite = null;
          this.assignments = [];
        }
        this.msg = 'Chantier supprimé';
        this.load();
      },
      error: () => (this.err = 'Erreur suppression'),
    });
  }

  selectWorksite(w: Worksite) {
    this.selectedWorksite = w;
    this.msg = `Chantier sélectionné: ${w.name}`;
    this.refreshAssignments();
    this.refreshAdminTimeline();
  }

  refreshAssignments() {
    if (!this.selectedWorksite) return;
    this.ws.adminListAssignments(this.selectedWorksite.id).subscribe({
      next: (res) => (this.assignments = res),
      error: () => (this.err = 'Erreur chargement affectations'),
    });
  }

  refreshAdminTimeline() {
    if (!this.selectedWorksite) return;
    this.ws.adminTimeline(this.selectedWorksite.id).subscribe({
      next: (res) => (this.adminTimelineItems = res ?? []),
      error: () => (this.err = 'Erreur chargement timeline admin'),
    });
  }

  isAssigned(userId: string): boolean {
    return this.assignments.some((a) => a.userId === userId);
  }

  assign(userId: string) {
    if (!this.selectedWorksite) return;
    this.msg = '';
    this.err = '';
    this.ws.adminAssign(this.selectedWorksite.id, userId).subscribe({
      next: () => {
        this.msg = 'Employé assigné';
        this.refreshAssignments();
        this.refreshAdminTimeline();
      },
      error: (e) => (this.err = e?.status === 403 ? 'ADMIN requis' : 'Erreur assignation'),
    });
  }

  unassign(userId: string) {
    if (!this.selectedWorksite) return;
    this.msg = '';
    this.err = '';
    this.ws.adminUnassign(this.selectedWorksite.id, userId).subscribe({
      next: () => {
        this.msg = 'Employé retiré';
        this.refreshAssignments();
        this.refreshAdminTimeline();
      },
      error: (e) => (this.err = e?.status === 403 ? 'ADMIN requis' : 'Erreur retrait'),
    });
  }

  employeeName(userId: string): string {
    return this.employeesById.get(userId)?.username ?? '(Utilisateur inconnu)';
  }

  employeeEmail(userId: string): string {
    return this.employeesById.get(userId)?.email ?? '';
  }
}

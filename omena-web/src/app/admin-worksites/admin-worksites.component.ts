import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { WorksiteService, Worksite, Assignment, TimelineItem } from '../worksite/worksite.service';
import { AuthService } from '../auth/auth.service';
import { forkJoin } from 'rxjs';

type EmployeeDto = { id: string; email: string; username: string; createdAt: string };

@Component({
  standalone: true,
  selector: 'app-admin-worksites',
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen p-6 bg-gray-50">
      <div class="max-w-7xl mx-auto rounded-2xl shadow-lg p-6 bg-white">
        <h1 class="text-2xl font-bold mb-6 text-gray-800">Admin — Gestion des Chantiers</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-3 mb-6 bg-gray-100 p-4 rounded-xl">
          <input class="border rounded-xl p-3 outline-none focus:ring-2 focus:ring-blue-500"
                 placeholder="Nom du chantier" [(ngModel)]="name" />
          <input class="border rounded-xl p-3 outline-none focus:ring-2 focus:ring-blue-500"
                 placeholder="Adresse" [(ngModel)]="address" />
          <button class="bg-blue-600 text-white font-semibold rounded-xl p-3 hover:bg-blue-700 transition"
                  (click)="create()">Créer chantier</button>
        </div>

        <div *ngIf="msg" class="p-3 mb-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded-lg">{{ msg }}</div>
        <div *ngIf="err" class="p-3 mb-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg">{{ err }}</div>

        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">

          <div class="border rounded-xl p-4 bg-gray-50">
            <h2 class="font-bold text-lg mb-4">🏗️ Chantiers</h2>
            <div class="space-y-3">
              <div *ngFor="let w of worksites"
                   class="bg-white border rounded-xl p-3 shadow-sm"
                   [class.border-blue-500]="selectedWorksite?.id === w.id">
                <div class="font-semibold">{{ w.name }}</div>
                <div class="text-sm text-gray-500">{{ w.address }}</div>
                <div class="flex gap-2 mt-3">
                  <button class="flex-1 text-sm border rounded-lg py-1 px-2"
                          [class.bg-blue-600]="selectedWorksite?.id === w.id"
                          [class.text-white]="selectedWorksite?.id === w.id"
                          (click)="selectWorksite(w)">
                    {{ selectedWorksite?.id === w.id ? 'Sélectionné' : 'Sélectionner' }}
                  </button>
                  <button class="text-sm text-red-500 border rounded-lg py-1 px-2" (click)="delWorksite(w.id)">Suppr.</button>
                </div>
              </div>
            </div>
          </div>

          <div class="border rounded-xl p-4 bg-gray-50">
            <h2 class="font-bold text-lg mb-4">👥 Assignés</h2>
            <div *ngIf="!selectedWorksite" class="text-gray-400 italic text-sm">Sélectionnez un chantier...</div>
            <div class="space-y-2">
              <div *ngFor="let a of assignments" class="bg-white p-3 rounded-lg border text-sm">
                <b>{{ employeeName(a.userId) }}</b>
                <div class="text-xs text-gray-500">{{ employeeEmail(a.userId) }}</div>
                <button (click)="unassign(a.userId)" class="text-red-500 text-xs mt-2 underline">Retirer</button>
              </div>
            </div>
          </div>

          <div class="border rounded-xl p-4 bg-gray-50">
            <h2 class="font-bold text-lg mb-4">📈 Activité</h2>
            <div *ngIf="!selectedWorksite" class="text-gray-400 italic text-sm">Aucun chantier...</div>
            <div class="space-y-2">
              <div *ngFor="let it of adminTimelineItems" class="bg-white p-2 rounded border text-xs">
                <span class="font-bold">{{ it.kind }}</span> - {{ it.at | date:'shortTime' }}
                <div class="mt-1">{{ employeeName(it.userId) }}</div>
                <div class="italic text-blue-600" *ngIf="it.note">{{ it.note }} ({{it.percent}}%)</div>
              </div>
            </div>
          </div>

          <div class="border rounded-xl p-4 bg-gray-50">
            <h2 class="font-bold text-lg mb-4">➕ Ajouter</h2>
            <div class="space-y-2">
              <div *ngFor="let e of employees" class="bg-white p-3 rounded-lg border text-sm">
                <div>{{ e.username }}</div>
                <button [disabled]="!selectedWorksite || isAssigned(e.id)"
                        (click)="assign(e.id)"
                        class="w-full mt-2 text-xs py-1 rounded border"
                        [class.bg-green-50]="!isAssigned(e.id)">
                  {{ isAssigned(e.id) ? 'Déjà présent' : 'Ajouter' }}
                </button>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  `,
})
export class AdminWorksitesComponent implements OnInit {
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
    private cdr: ChangeDetectorRef // Ajouté pour forcer le rafraîchissement
  ) {}

  ngOnInit() {
    this.loadAllInitialData();
  }

  // Utilisation de forkJoin pour être sûr que TOUT arrive en même temps au chargement
  loadAllInitialData() {
    forkJoin({
      worksites: this.ws.adminListWorksites(),
      employees: this.auth.adminListEmployees()
    }).subscribe({
      next: (data: any) => {
        this.worksites = data.worksites;
        this.employees = data.employees;
        this.employeesById = new Map(this.employees.map((e) => [e.id, e]));
        this.cdr.detectChanges(); // Force Angular à dessiner la liste
      },
      error: () => (this.err = 'Erreur lors du chargement initial')
    });
  }

  selectWorksite(w: Worksite) {
    // 1. On assigne immédiatement
    this.selectedWorksite = w;
    this.assignments = []; // On vide pour l'effet visuel de chargement
    this.adminTimelineItems = [];

    // 2. On déclenche le chargement des données liées
    this.refreshWorksiteDetails(w.id);
  }

  refreshWorksiteDetails(worksiteId: string) {
    // On lance les deux appels en parallèle
    forkJoin({
      assigns: this.ws.adminListAssignments(worksiteId),
      timeline: this.ws.adminTimeline(worksiteId)
    }).subscribe({
      next: (res: any) => {
        this.assignments = res.assigns;
        this.adminTimelineItems = res.timeline ?? [];
        this.cdr.detectChanges(); // Force la mise à jour de l'UI
      },
      error: () => (this.err = 'Erreur chargement des détails du chantier')
    });
  }

  // --- ACTIONS ---

  create() {
    if (!this.name.trim()) return;
    this.ws.adminCreateWorksite(this.name, this.address).subscribe(() => {
      this.msg = 'Chantier créé';
      this.name = ''; this.address = '';
      this.loadAllInitialData();
    });
  }

  assign(userId: string) {
    if (!this.selectedWorksite) return;
    this.ws.adminAssign(this.selectedWorksite.id, userId).subscribe(() => {
      this.refreshWorksiteDetails(this.selectedWorksite!.id);
    });
  }

  unassign(userId: string) {
    if (!this.selectedWorksite) return;
    this.ws.adminUnassign(this.selectedWorksite.id, userId).subscribe(() => {
      this.refreshWorksiteDetails(this.selectedWorksite!.id);
    });
  }

  delWorksite(id: string) {
    if (!confirm('Supprimer ?')) return;
    this.ws.adminDeleteWorksite(id).subscribe(() => {
      this.selectedWorksite = null;
      this.loadAllInitialData();
    });
  }

  isAssigned(userId: string): boolean {
    return this.assignments.some((a) => a.userId === userId);
  }

  employeeName(userId: string): string {
    return this.employeesById.get(userId)?.username ?? 'Inconnu';
  }

  employeeEmail(userId: string): string {
    return this.employeesById.get(userId)?.email ?? '';
  }
}

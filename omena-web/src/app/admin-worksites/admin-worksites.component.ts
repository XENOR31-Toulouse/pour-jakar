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
    <div class="min-h-screen p-4 md:p-8 bg-gray-50 text-gray-900">
      <div class="max-w-7xl mx-auto rounded-3xl shadow-xl p-6 bg-white border border-gray-100">

        <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
          <div>
            <h1 class="text-3xl font-extrabold tracking-tight">Gestion des <span class="text-blue-600">Chantiers</span></h1>
            <p class="text-gray-500 text-sm mt-1 font-medium italic">Administration et affectations en temps réel</p>
          </div>
          <a routerLink="/" class="px-5 py-2 bg-white border border-gray-200 rounded-2xl text-sm font-bold shadow-sm hover:bg-gray-50 transition-all flex items-center gap-2">
            ⬅️ Dashboard
          </a>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8 bg-slate-900 p-6 rounded-3xl shadow-lg shadow-slate-200">
          <div class="md:col-span-1">
             <input class="w-full bg-slate-800 border border-slate-700 rounded-2xl p-3 text-white placeholder-slate-500 outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                   placeholder="Nom du chantier" [(ngModel)]="name" />
          </div>
          <div class="md:col-span-2">
            <input class="w-full bg-slate-800 border border-slate-700 rounded-2xl p-3 text-white placeholder-slate-500 outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                   placeholder="Adresse complète" [(ngModel)]="address" />
          </div>
          <button class="w-full bg-blue-600 text-white font-bold rounded-2xl p-3 hover:bg-blue-700 transition-all active:scale-95 shadow-lg shadow-blue-900/20"
                  (click)="create()">
            + Créer
          </button>
        </div>

        <div *ngIf="msg" class="p-4 mb-6 text-sm font-bold text-green-700 bg-green-50 border border-green-100 rounded-2xl animate-pulse text-center">✅ {{ msg }}</div>
        <div *ngIf="err" class="p-4 mb-6 text-sm font-bold text-red-600 bg-red-50 border border-red-100 rounded-2xl text-center">⚠️ {{ err }}</div>

        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">

          <div class="flex flex-col gap-4">
            <h2 class="font-black text-xs uppercase tracking-widest text-gray-400 px-2">🏗️ Liste des sites</h2>
            <div class="space-y-3 max-h-[600px] overflow-y-auto pr-2 font-sans">
              <div *ngFor="let w of worksites"
                   class="group bg-white border rounded-2xl p-4 shadow-sm transition-all hover:shadow-md cursor-pointer"
                   [class.border-blue-500]="selectedWorksite?.id === w.id"
                   [class.ring-2]="selectedWorksite?.id === w.id"
                   [class.ring-blue-100]="selectedWorksite?.id === w.id"
                   (click)="selectWorksite(w)">
                <div class="font-bold text-gray-800 group-hover:text-blue-600 transition-colors">{{ w.name }}</div>
                <div class="text-xs text-gray-400 mt-1 truncate">{{ w.address }}</div>
                <div class="flex gap-2 mt-4">
                  <button class="flex-1 text-[10px] font-bold uppercase tracking-tighter border rounded-xl py-2 px-2 transition-all"
                          [class.bg-blue-600]="selectedWorksite?.id === w.id"
                          [class.text-white]="selectedWorksite?.id === w.id"
                          [class.border-blue-600]="selectedWorksite?.id === w.id">
                    {{ selectedWorksite?.id === w.id ? 'Sélectionné' : 'Voir Détails' }}
                  </button>
                  <button (click)="$event.stopPropagation(); delWorksite(w.id)"
                          class="text-red-300 hover:text-red-600 transition-colors p-2 text-sm">
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <h2 class="font-black text-xs uppercase tracking-widest text-gray-400 px-2">👥 Équipe sur place</h2>
            <div class="bg-gray-50 border border-dashed border-gray-200 rounded-3xl p-4 min-h-[300px]">

              <div *ngIf="selectedWorksite" class="mb-4">
                <button *ngIf="!isAssigned(auth.getUserId() || '')"
                        (click)="assignMe()"
                        class="w-full bg-white border-2 border-blue-600 text-blue-600 font-bold py-3 rounded-2xl hover:bg-blue-50 active:scale-95 transition-all text-[10px] uppercase tracking-widest shadow-sm">
                  🙋‍♂️ M'ajouter à l'équipe
                </button>
                <button *ngIf="isAssigned(auth.getUserId() || '')"
                        (click)="unassign(auth.getUserId()!)"
                        class="w-full bg-blue-50 border-2 border-blue-200 text-blue-700 font-bold py-3 rounded-2xl hover:bg-blue-100 active:scale-95 transition-all text-[10px] uppercase tracking-widest shadow-sm">
                  🚶‍♂️ Me retirer de l'équipe
                </button>
              </div>

              <div *ngIf="!selectedWorksite" class="flex flex-col items-center justify-center h-full text-center p-6 text-gray-400">
                <span class="text-3xl mb-2">👈</span>
                <p class="text-xs font-medium leading-relaxed">Choisissez un chantier</p>
              </div>

              <div class="space-y-2">
                <div *ngFor="let a of assignments" class="bg-white p-3 rounded-2xl border border-gray-100 shadow-sm flex flex-col group">
                  <div class="flex justify-between items-start">
                    <span class="font-bold text-sm text-gray-800">{{ employeeName(a.userId) }}</span>
                    <span *ngIf="a.userId === auth.getUserId()" class="text-[8px] bg-blue-100 text-blue-600 px-1.5 py-0.5 rounded font-black uppercase">Moi</span>
                  </div>
                  <span class="text-[10px] text-gray-400 font-mono">{{ employeeEmail(a.userId) }}</span>
                  <button (click)="unassign(a.userId)" class="mt-2 text-[10px] font-bold text-red-400 hover:text-red-600 text-left transition-colors uppercase">
                    ✖ Retirer
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <h2 class="font-black text-xs uppercase tracking-widest text-gray-400 px-2">📈 Activité</h2>
            <div class="bg-gray-50 border border-dashed border-gray-200 rounded-3xl p-4 min-h-[300px]">
               <div *ngIf="!selectedWorksite" class="flex flex-col items-center justify-center h-full text-center p-6 text-gray-400">
                <span class="text-3xl mb-2">⏱️</span>
                <p class="text-xs font-medium">Historique d'activité</p>
              </div>
              <div class="space-y-3">
                <div *ngFor="let it of adminTimelineItems" class="relative pl-4 border-l-2 border-blue-200 py-1">
                  <div class="absolute -left-[5px] top-2 h-2 w-2 rounded-full bg-blue-500 shadow-[0_0_8px_rgba(59,130,246,0.5)]"></div>
                  <div class="text-[10px] font-bold text-blue-600 uppercase tracking-tighter">
                    {{ it.kind }} <span class="text-gray-400 ml-1 font-normal">• {{ it.at | date:'shortTime' }}</span>
                  </div>
                  <div class="text-xs font-bold text-gray-700">{{ employeeName(it.userId) }}</div>
                  <div class="bg-white p-2 rounded-xl border border-gray-100 mt-1 text-[11px] italic text-gray-600 shadow-sm" *ngIf="it.note">
                    {{ it.note }} <span class="font-black text-blue-600 ml-1">({{it.percent}}%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <h2 class="font-black text-xs uppercase tracking-widest text-gray-400 px-2">➕ Ajouter un employé</h2>
            <div class="bg-white border border-gray-100 shadow-sm rounded-3xl p-4 space-y-2 max-h-[600px] overflow-y-auto">
              <div *ngFor="let e of employees" class="p-3 rounded-2xl border border-gray-50 bg-gray-50/50 hover:bg-gray-50 transition-colors">
                <div class="font-bold text-xs truncate text-gray-800">{{ e.username }}</div>
                <button [disabled]="!selectedWorksite || isAssigned(e.id)"
                        (click)="assign(e.id)"
                        class="w-full mt-2 text-[10px] font-bold uppercase py-2 rounded-xl border transition-all shadow-sm"
                        [class.bg-white]="!isAssigned(e.id)"
                        [class.text-blue-600]="!isAssigned(e.id)"
                        [class.bg-gray-100]="isAssigned(e.id)"
                        [class.text-gray-400]="isAssigned(e.id)">
                  {{ isAssigned(e.id) ? 'Déjà présent' : 'Assigner' }}
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
    public auth: AuthService, // public pour y accéder dans le template
    private ws: WorksiteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadAllInitialData();
  }

  loadAllInitialData() {
    forkJoin({
      worksites: this.ws.adminListWorksites(),
      employees: this.auth.adminListEmployees()
    }).subscribe({
      next: (data: any) => {
        this.worksites = data.worksites;
        this.employees = data.employees;
        this.employeesById = new Map(this.employees.map((e) => [e.id, e]));
        this.cdr.detectChanges();
      },
      error: () => (this.err = 'Erreur lors du chargement initial')
    });
  }

  selectWorksite(w: Worksite) {
    this.selectedWorksite = w;
    this.assignments = [];
    this.adminTimelineItems = [];
    this.refreshWorksiteDetails(w.id);
  }

  refreshWorksiteDetails(worksiteId: string) {
    forkJoin({
      assigns: this.ws.adminListAssignments(worksiteId),
      timeline: this.ws.adminTimeline(worksiteId)
    }).subscribe({
      next: (res: any) => {
        this.assignments = res.assigns;
        this.adminTimelineItems = res.timeline ?? [];
        this.cdr.detectChanges();
      },
      error: () => (this.err = 'Erreur chargement des détails')
    });
  }

  create() {
    if (!this.name.trim()) return;
    this.ws.adminCreateWorksite(this.name, this.address).subscribe({
      next: () => {
        this.msg = 'Chantier créé avec succès';
        this.name = ''; this.address = '';
        this.loadAllInitialData();
        this.clearMsg();
      }
    });
  }

  assign(userId: string) {
    if (!this.selectedWorksite) return;
    this.ws.adminAssign(this.selectedWorksite.id, userId).subscribe({
      next: () => this.refreshWorksiteDetails(this.selectedWorksite!.id)
    });
  }

  assignMe() {
    if (!this.selectedWorksite) return;
    const myId = this.auth.getUserId();
    if (!myId) return;

    this.ws.adminAssign(this.selectedWorksite.id, myId).subscribe({
      next: () => {
        this.msg = "Vous avez été ajouté au chantier";
        this.refreshWorksiteDetails(this.selectedWorksite!.id);
        this.clearMsg();
      }
    });
  }

  unassign(userId: string) {
    if (!this.selectedWorksite) return;
    this.ws.adminUnassign(this.selectedWorksite.id, userId).subscribe({
      next: () => {
        if (userId === this.auth.getUserId()) this.msg = "Vous avez quitté le chantier";
        this.refreshWorksiteDetails(this.selectedWorksite!.id);
        this.clearMsg();
      }
    });
  }

  delWorksite(id: string) {
    if (!confirm('Voulez-vous vraiment supprimer ce chantier ?')) return;
    this.ws.adminDeleteWorksite(id).subscribe({
      next: () => {
        this.selectedWorksite = null;
        this.msg = 'Chantier supprimé';
        this.loadAllInitialData();
        this.clearMsg();
      }
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

  private clearMsg() {
    setTimeout(() => { this.msg = ''; this.err = ''; }, 3000);
  }
}

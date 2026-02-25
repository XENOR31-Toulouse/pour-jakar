import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core'; // 1. Import ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { WorksiteService, TimelineItem } from '../worksite/worksite.service';
import { Subscription } from 'rxjs';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-4 md:p-8 text-gray-900">
      <div class="max-w-3xl mx-auto">
        <div class="flex items-center justify-between mb-6">
          <a routerLink="/my-worksites" class="text-blue-600 font-bold text-sm flex items-center gap-1 hover:underline">⬅️ Retour</a>
          <button (click)="load()" class="text-xs bg-white border border-gray-200 px-3 py-1 rounded-full font-bold text-gray-400 hover:text-blue-600 transition-colors">
            🔄 Actualiser
          </button>
        </div>

        <div class="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-6">
          <div class="bg-slate-900 p-6 text-white">
            <h1 class="text-2xl font-bold italic">Suivi du <span class="text-blue-400">Chantier</span></h1>
            <p class="text-[10px] text-slate-400 font-mono mt-2 opacity-70">REF: {{ worksiteId }}</p>
          </div>

          <div class="p-6">
            <div class="grid grid-cols-2 gap-4 mb-8">
              <button (click)="arrival()" class="flex flex-col items-center justify-center p-4 bg-green-50 border border-green-100 rounded-2xl active:scale-95 group">
                <span class="text-3xl mb-2 group-hover:scale-110 transition-transform">📍</span>
                <span class="font-black text-green-700 uppercase text-[10px] tracking-widest">Arrivée</span>
              </button>
              <button (click)="departure()" class="flex flex-col items-center justify-center p-4 bg-amber-50 border border-amber-100 rounded-2xl active:scale-95 group">
                <span class="text-3xl mb-2 group-hover:scale-110 transition-transform">🚗</span>
                <span class="font-black text-amber-700 uppercase text-[10px] tracking-widest">Départ</span>
              </button>
            </div>

            <div class="bg-gray-50 rounded-2xl p-4 border border-gray-100">
              <textarea class="w-full bg-white border border-gray-200 rounded-xl p-3 text-sm focus:ring-2 focus:ring-blue-500 outline-none" rows="3" placeholder="Rapport..." [(ngModel)]="note"></textarea>
              <div class="flex gap-2 mt-3">
                <input class="flex-1 bg-white border border-gray-200 rounded-xl p-3 text-sm" type="number" placeholder="%" [(ngModel)]="percent" />
                <button (click)="progress()" class="bg-blue-600 text-white font-bold px-6 rounded-xl hover:bg-blue-700 shadow-lg">Envoyer</button>
              </div>
            </div>

            <div *ngIf="msg" class="mt-4 p-3 bg-green-100 text-green-700 rounded-xl text-xs font-bold text-center">✅ {{ msg }}</div>
            <div *ngIf="err" class="mt-4 p-3 bg-red-100 text-red-700 rounded-xl text-xs font-bold text-center">⚠️ {{ err }}</div>
          </div>
        </div>

        <div class="space-y-4">
          <div *ngFor="let it of items" class="bg-white border border-gray-100 rounded-2xl p-4 shadow-sm flex gap-4 items-start">
             <div class="h-10 w-10 shrink-0 rounded-full flex items-center justify-center text-sm" [ngClass]="{'bg-green-100 text-green-600': it.kind === 'ARRIVAL','bg-amber-100 text-amber-600': it.kind === 'DEPARTURE','bg-blue-100 text-blue-600': it.kind === 'PROGRESS'}">
                {{ it.kind === 'ARRIVAL' ? 'IN' : it.kind === 'DEPARTURE' ? 'OUT' : '📝' }}
             </div>
             <div class="flex-1">
               <div class="flex justify-between font-black text-[10px] uppercase">
                 <span [ngClass]="{'text-green-600': it.kind === 'ARRIVAL','text-amber-600': it.kind === 'DEPARTURE','text-blue-600': it.kind === 'PROGRESS'}">{{ it.kind }}</span>
                 <span class="text-gray-400">{{ it.at | date:'HH:mm' }}</span>
               </div>
               <div class="text-gray-800 text-sm mt-1">{{ it.note }} <span *ngIf="it.percent" class="text-blue-600">({{it.percent}}%)</span></div>
             </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class WorksiteDetailComponent implements OnInit, OnDestroy {
  worksiteId = '';
  items: TimelineItem[] = [];
  note = '';
  percent: number | null = null;
  msg = '';
  err = '';
  private sub?: Subscription;

  // 2. Injecter ChangeDetectorRef
  constructor(
    private route: ActivatedRoute,
    private ws: WorksiteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe(params => {
      this.worksiteId = params.get('id') ?? '';
      this.load();
    });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  load() {
    this.ws.myTimeline(this.worksiteId).subscribe({
      next: (res) => {
        // 🔥 On force une nouvelle référence de tableau et un tri par date (plus récent en haut)
        this.items = [...(res ?? [])].sort((a, b) =>
          new Date(b.at).getTime() - new Date(a.at).getTime()
        );
        this.cdr.detectChanges(); // 🔥 On force Angular à rafraîchir la vue
      },
      error: (e) => this.err = "Erreur de chargement"
    });
  }

  arrival() {
    this.ws.arrival(this.worksiteId).subscribe({
      next: () => {
        this.msg = 'Pointage ARRIVÉE validé';
        this.load(); // Relance le chargement
        this.clearMsg();
      }
    });
  }

  departure() {
    this.ws.departure(this.worksiteId).subscribe({
      next: () => {
        this.msg = 'Pointage DÉPART validé';
        this.load();
        this.clearMsg();
      }
    });
  }

  progress() {
    const noteValue = (this.note || '').trim();
    if (!noteValue) return;

    this.ws.addProgress(this.worksiteId, noteValue, this.percent).subscribe({
      next: () => {
        this.msg = 'Rapport envoyé';
        this.note = '';
        this.percent = null;

        // Optionnel : Ajout "Optimiste" immédiat avant même que load() ne revienne
        // pour que l'utilisateur voit son message tout de suite.
        this.load();
        this.clearMsg();
      }
    });
  }

  private clearMsg() {
    setTimeout(() => { this.msg = ''; this.cdr.detectChanges(); }, 3000);
  }
}

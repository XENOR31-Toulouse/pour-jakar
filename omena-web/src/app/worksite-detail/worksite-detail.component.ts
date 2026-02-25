import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { WorksiteService, TimelineItem } from '../worksite/worksite.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
  <div class="min-h-screen p-6">
    <div class="max-w-3xl mx-auto rounded-2xl shadow p-6 bg-white">
      <h1 class="text-2xl font-semibold mb-4">Chantier</h1>

      <p class="text-sm mb-4">worksiteId: <span class="break-all">{{ worksiteId }}</span></p>

      <div class="flex flex-wrap gap-2 mb-4">
        <button class="rounded-xl p-3 border" (click)="arrival()">Arrivée</button>
        <button class="rounded-xl p-3 border" (click)="departure()">Départ</button>
        <button class="rounded-xl p-3 border" (click)="load()">Rafraîchir</button>
        <a class="rounded-xl p-3 border inline-block" routerLink="/my-worksites">Retour</a>
      </div>

      <div class="border rounded-xl p-4 mb-4">
        <h2 class="font-semibold mb-2">Avancement</h2>

        <textarea class="w-full border rounded-xl p-3" rows="3"
          placeholder="Note d'avancement"
          [(ngModel)]="note"></textarea>

        <div class="flex gap-2 mt-2">
          <input class="border rounded-xl p-3 w-40" type="number" min="0" max="100"
            placeholder="% (optionnel)"
            [(ngModel)]="percent" />
          <button class="rounded-xl p-3 border" (click)="progress()">Envoyer</button>
        </div>
      </div>

      <p *ngIf="msg" class="text-sm text-green-700 mb-3">{{ msg }}</p>
      <p *ngIf="err" class="text-sm text-red-600 mb-3">{{ err }}</p>

      <div class="border rounded-xl p-4">
        <h2 class="font-semibold mb-2">Timeline</h2>

        <div *ngIf="items.length === 0" class="text-sm opacity-70">Aucun événement.</div>

        <div *ngFor="let it of items" class="border rounded-xl p-3 mb-2">
          <div class="font-semibold">
            {{ it.kind }}
            <span class="text-sm opacity-70">— {{ it.at | date:'medium' }}</span>
          </div>

          <div class="text-sm mt-1" *ngIf="it.kind === 'PROGRESS'">
            {{ it.note }}
            <span *ngIf="it.percent !== null && it.percent !== undefined"> ({{ it.percent }}%)</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  `
})
export class WorksiteDetailComponent {
  worksiteId = '';
  items: TimelineItem[] = [];
  note = '';
  percent: number | null = null;
  msg = '';
  err = '';

  constructor(private route: ActivatedRoute, private ws: WorksiteService) {
    this.worksiteId = this.route.snapshot.paramMap.get('id') ?? '';
  }

  ngOnInit() { this.load(); }

  load() {
    this.msg = ''; this.err = '';
    this.ws.myTimeline(this.worksiteId).subscribe({
      next: (res) => this.items = res,
      error: (e) => {
        if (e?.status === 403) this.err = "Tu n'es pas assigné à ce chantier";
        else if (e?.status === 401) this.err = "Non connecté";
        else this.err = "Erreur chargement";
      }
    });
  }

  arrival() {
    this.msg = ''; this.err = '';
    this.ws.arrival(this.worksiteId).subscribe({
      next: () => { this.msg = 'Arrivée enregistrée'; this.load(); },
      error: (e) => this.err = e?.status === 403 ? "Non assigné" : "Erreur arrivée"
    });
  }

  departure() {
    this.msg = ''; this.err = '';
    this.ws.departure(this.worksiteId).subscribe({
      next: () => { this.msg = 'Départ enregistré'; this.load(); },
      error: (e) => this.err = e?.status === 403 ? "Non assigné" : "Erreur départ"
    });
  }

  progress() {
    const note = (this.note || '').trim();
    if (!note) { this.err = 'Note obligatoire'; return; }

    this.msg = ''; this.err = '';
    this.ws.addProgress(this.worksiteId, note, this.percent).subscribe({
      next: () => { this.msg = 'Avancement envoyé'; this.note=''; this.percent=null; this.load(); },
      error: (e) => this.err = e?.status === 403 ? "Non assigné" : "Erreur avancement"
    });
  }
}

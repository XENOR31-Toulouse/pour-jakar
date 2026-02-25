import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WorksiteService, Worksite } from '../worksite/worksite.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen p-6">
      <div class="max-w-2xl mx-auto rounded-2xl shadow p-6 bg-white">
        <div class="flex items-center justify-between gap-2 mb-4">
          <h1 class="text-2xl font-semibold">Mes chantiers</h1>
          <div class="flex gap-2">
            <button class="rounded-xl p-3 border" (click)="load()">Rafraîchir</button>
            <a class="rounded-xl p-3 border inline-block" routerLink="/">Accueil</a>
          </div>
        </div>

        <p *ngIf="error" class="text-sm text-red-600 mb-3">{{ error }}</p>

        <div *ngIf="loading" class="text-sm opacity-70">Chargement...</div>

        <ul class="space-y-2" *ngIf="!loading && items.length">
          <li class="border rounded-xl p-3" *ngFor="let w of items">
            <div class="flex items-start justify-between gap-3">
              <div class="min-w-0">
                <div class="font-semibold truncate">{{ w.name }}</div>
                <div class="text-sm opacity-70 truncate">{{ w.address || '-' }}</div>
                <div class="text-xs opacity-60 break-all mt-1">id: {{ w.id }}</div>
              </div>

              <a
                class="underline text-sm whitespace-nowrap"
                [routerLink]="['/worksites', w.id]"
              >
                Ouvrir
              </a>
            </div>
          </li>
        </ul>

        <div *ngIf="!loading && !items.length && !error" class="text-sm opacity-70">
          Aucun chantier assigné.
        </div>
      </div>
    </div>
  `
})
export class MyWorksitesComponent {
  items: Worksite[] = [];
  loading = false;
  error = '';

constructor(private ws: WorksiteService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
  console.log('MY_WORKSITES_COMPONENT_USED');
    this.load();
  }

load() {
  console.log('[MyWorksites] load start');
  this.loading = true;
  this.error = '';

  this.ws.myWorksites().subscribe({
    next: (res) => {
  this.items = res ?? [];
  this.loading = false;
  this.cdr.detectChanges();
    },
    error: (e) => {
  this.loading = false;
  // ... ton code error
  this.cdr.detectChanges();
    },
  });
}
}

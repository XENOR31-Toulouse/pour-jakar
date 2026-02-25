import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WorksiteService, Worksite } from '../worksite/worksite.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 p-4 md:p-8 text-gray-900">
      <div class="max-w-2xl mx-auto">

        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-3xl font-extrabold tracking-tight">Mes <span class="text-blue-600">Chantiers</span></h1>
            <p class="text-gray-500 text-sm font-medium italic mt-1" *ngIf="items.length > 0">
              {{ items.length }} site(s) actif(s)
            </p>
          </div>
          <div class="flex gap-2">
            <button (click)="load()"
                    class="p-3 bg-white border border-gray-200 rounded-2xl shadow-sm hover:bg-gray-50 active:scale-95 transition-all">
              🔄
            </button>
            <a routerLink="/"
               class="p-3 bg-white border border-gray-200 rounded-2xl shadow-sm hover:bg-gray-50 active:scale-95 transition-all text-sm font-bold">
              Accueil
            </a>
          </div>
        </div>

        <div *ngIf="error" class="mb-6 p-4 bg-red-50 border border-red-100 text-red-600 rounded-2xl text-sm font-bold flex items-center gap-2">
          <span>⚠️</span> {{ error }}
        </div>

        <div *ngIf="loading" class="space-y-4">
          <div *ngFor="let i of [1,2,3]" class="h-32 bg-gray-200/50 animate-pulse rounded-3xl"></div>
        </div>

        <div class="space-y-4" *ngIf="!loading && items.length">
          <div *ngFor="let w of items"
               class="group relative bg-white border border-gray-100 rounded-3xl p-6 shadow-sm hover:shadow-md transition-all active:ring-2 active:ring-blue-100">

            <div class="flex justify-between items-start gap-4">
              <div class="min-w-0">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-xl">🏗️</span>
                  <h2 class="font-black text-lg text-gray-800 truncate">{{ w.name }}</h2>
                </div>

                <div class="flex items-start gap-1 text-gray-500 mb-4">
                  <span class="text-sm">📍</span>
                  <p class="text-sm font-medium leading-snug">{{ w.address || 'Adresse non renseignée' }}</p>
                </div>

                <div class="inline-flex items-center px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-[10px] font-bold uppercase tracking-widest">
                  ID: {{ w.id.substring(0, 8) }}...
                </div>
              </div>

              <a [routerLink]="['/worksites', w.id]"
                 class="flex items-center justify-center h-12 w-12 bg-blue-600 text-white rounded-2xl shadow-lg shadow-blue-200 hover:bg-blue-700 transition-colors">
                <span class="text-xl">➔</span>
              </a>
            </div>

            <a [routerLink]="['/worksites', w.id]" class="absolute inset-0 z-0"></a>
          </div>
        </div>

        <div *ngIf="!loading && !items.length && !error"
             class="flex flex-col items-center justify-center py-20 bg-white border border-dashed border-gray-200 rounded-3xl text-center">
          <span class="text-5xl mb-4">👷‍♂️</span>
          <h3 class="text-lg font-bold text-gray-800">Aucun chantier assigné</h3>
          <p class="text-sm text-gray-500 mt-1">Contactez votre administrateur pour <br> être ajouté à un projet.</p>
        </div>

      </div>
    </div>
  `
})
export class MyWorksitesComponent implements OnInit {
  items: Worksite[] = [];
  loading = false;
  error = '';

  constructor(private ws: WorksiteService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.load();
  }

  load() {
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
        this.error = "Impossible de récupérer vos chantiers. Vérifiez votre connexion.";
        this.cdr.detectChanges();
      },
    });
  }
}

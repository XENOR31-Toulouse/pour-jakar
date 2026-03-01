import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center p-6">
      <div class="max-w-md w-full rounded-2xl shadow p-6 bg-white">
        <h1 class="text-2xl font-semibold mb-2">Zone protégée</h1>
        <p>✅ Tu es connecté, donc tu vois ce texte.</p>
      </div>
    </div>
  `
})
export class ProtectedComponent {}

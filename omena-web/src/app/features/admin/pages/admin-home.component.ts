import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminNavComponent } from '../components/admin-nav.component';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [RouterModule, AdminNavComponent],
  template: `
    <app-admin-nav></app-admin-nav>
    <div class="container mx-auto p-4">
      <h1 class="text-3xl font-bold mb-6">Tableau de bord Admin</h1>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <a routerLink="/admin/worksites" class="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow">
          <div class="text-3xl mb-2">🏗️</div>
          <h2 class="text-xl font-bold text-gray-800">Chantiers</h2>
          <p class="text-gray-600">Gérer les chantiers</p>
        </a>
        
        <a routerLink="/admin/employees" class="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow">
          <div class="text-3xl mb-2">👥</div>
          <h2 class="text-xl font-bold text-gray-800">Employés</h2>
          <p class="text-gray-600">Gérer les employés</p>
        </a>
        
        <a routerLink="/admin/clients" class="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow">
          <div class="text-3xl mb-2">🏢</div>
          <h2 class="text-xl font-bold text-gray-800">Clients</h2>
          <p class="text-gray-600">Gérer les clients</p>
        </a>
        
        <a routerLink="/admin/create-user" class="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow">
          <div class="text-3xl mb-2">👤</div>
          <h2 class="text-xl font-bold text-gray-800">Utilisateurs</h2>
          <p class="text-gray-600">Créer des utilisateurs</p>
        </a>
      </div>
    </div>
  `,
  styles: []
})
export class AdminHomeComponent {

}
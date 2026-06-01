import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminNavComponent } from '../../components/admin-nav.component';

@Component({
  selector: 'app-admin-clients',
  standalone: true,
  imports: [RouterModule, AdminNavComponent],
  template: `
    <app-admin-nav></app-admin-nav>
    <div class="container mx-auto p-4">
      <h1 class="text-3xl font-bold mb-6">Gestion des Clients</h1>

      <div class="bg-white shadow rounded-lg p-6 mb-6">
        <p class="text-gray-700 mb-4">
          Cette page permet de gérer les clients dans le système.
        </p>
        <p class="text-gray-700">
          Vous pouvez consulter la liste des clients, en créer de nouveaux,
          modifier ou supprimer des clients existants.
        </p>
      </div>

      <div class="flex space-x-4">
        <a
          routerLink="/admin/clients"
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
          Voir tous les clients
        </a>
        <a
          routerLink="/admin/clients/create"
          class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
          Créer un nouveau client
        </a>
      </div>
    </div>
  `,
  styles: []
})
export class AdminClientsComponent {

}

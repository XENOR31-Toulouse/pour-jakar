import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ClientListComponent } from '../client-list/client-list.component';

@Component({
  selector: 'app-clients-page',
  standalone: true,
  imports: [RouterModule, ClientListComponent],
  template: `
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

      <app-client-list></app-client-list>
    </div>
  `,
  styles: []
})
export class ClientsPageComponent {

}
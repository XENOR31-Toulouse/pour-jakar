import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../../data/client.service';
import { Client } from '../../../data/client.service';
import { AsyncPipe } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [AsyncPipe, RouterModule],
  template: `
    <div class="container mx-auto p-4">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Clients</h1>
        <button 
          routerLink="/admin/clients/create" 
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
          Ajouter un client
        </button>
      </div>

      <div class="bg-white shadow rounded-lg overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Téléphone</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Adresse</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            @for (client of clients$ | async; track client.id) {
              <tr>
                <td class="px-6 py-4 whitespace-nowrap">{{ client.name }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ client.email }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ client.phoneNumber }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ client.address }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button 
                    (click)="deleteClient(client.id)"
                    class="text-red-600 hover:text-red-900 mr-3">
                    Supprimer
                  </button>
                  <button 
                    routerLink="/admin/clients/{{ client.id }}/edit"
                    class="text-blue-600 hover:text-blue-900">
                    Modifier
                  </button>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: []
})
export class ClientListComponent implements OnInit {
  clients$ = this.clientService.getAllClients();

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {}

  deleteClient(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.clientService.deleteClient(id).subscribe({
        next: () => {
          // Recharger la liste des clients après suppression
          this.clients$ = this.clientService.getAllClients();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression du client:', error);
          alert('Erreur lors de la suppression du client');
        }
      });
    }
  }
}
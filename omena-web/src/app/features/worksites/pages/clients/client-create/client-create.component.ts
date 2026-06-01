import { Component } from '@angular/core';
import { ClientService } from '../../../data/client.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-client-create',
  standalone: true,
  imports: [FormsModule, AsyncPipe],
  template: `
    <div class="container mx-auto p-4">
      <h1 class="text-2xl font-bold mb-6">Créer un nouveau client</h1>
      
      <form (ngSubmit)="onSubmit()" #clientForm="ngForm" class="bg-white shadow rounded-lg p-6">
        <div class="mb-4">
          <label for="name" class="block text-gray-700 text-sm font-bold mb-2">Nom *</label>
          <input 
            id="name"
            name="name"
            [(ngModel)]="client.name"
            required
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            placeholder="Nom du client">
        </div>

        <div class="mb-4">
          <label for="email" class="block text-gray-700 text-sm font-bold mb-2">Email *</label>
          <input 
            id="email"
            name="email"
            [(ngModel)]="client.email"
            type="email"
            required
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            placeholder="Email du client">
        </div>

        <div class="mb-4">
          <label for="phoneNumber" class="block text-gray-700 text-sm font-bold mb-2">Téléphone</label>
          <input 
            id="phoneNumber"
            name="phoneNumber"
            [(ngModel)]="client.phoneNumber"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            placeholder="Numéro de téléphone">
        </div>

        <div class="mb-4">
          <label for="address" class="block text-gray-700 text-sm font-bold mb-2">Adresse</label>
          <textarea 
            id="address"
            name="address"
            [(ngModel)]="client.address"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            placeholder="Adresse du client"
            rows="3"></textarea>
        </div>

        <div class="flex items-center justify-between">
          <button 
            type="button"
            (click)="cancel()"
            class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
            Annuler
          </button>
          <button 
            type="submit"
            [disabled]="!clientForm.valid"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50">
            Créer le client
          </button>
        </div>
      </form>
    </div>
  `,
  styles: []
})
export class ClientCreateComponent {
  client = {
    name: '',
    email: '',
    phoneNumber: '',
    address: ''
  };

  constructor(private clientService: ClientService, private router: Router) {}

  onSubmit(): void {
    this.clientService.createClient(this.client).subscribe({
      next: (createdClient) => {
        console.log('Client créé:', createdClient);
        this.router.navigate(['/admin/clients']);
      },
      error: (error) => {
        console.error('Erreur lors de la création du client:', error);
        alert('Erreur lors de la création du client');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/admin/clients']);
  }
}
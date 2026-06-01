import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-admin-nav',
  standalone: true,
  imports: [RouterModule],
  template: `
    <nav class="bg-white shadow-md">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <a routerLink="/admin" class="flex-shrink-0 flex items-center">
              <span class="text-xl font-bold text-blue-600">Omena</span>
            </a>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <a routerLink="/admin/worksites"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium text-gray-500 hover:text-gray-700 hover:border-gray-300">
                Chantiers
              </a>
              <a routerLink="/admin/employees"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium text-gray-500 hover:text-gray-700 hover:border-gray-300">
                Employés
              </a>
              <a routerLink="/admin/clients"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium text-gray-500 hover:text-gray-700 hover:border-gray-300">
                Clients
              </a>
              <a routerLink="/admin/create-user"
                 routerLinkActive="border-blue-500 text-gray-900"
                 class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium text-gray-500 hover:text-gray-700 hover:border-gray-300">
                Créer un utilisateur
              </a>
            </div>
          </div>
          <div class="flex items-center">
            <span class="text-sm text-gray-700 mr-4">Bonjour, {{ getUsername() }}</span>
            <button (click)="logout()" class="text-sm text-gray-500 hover:text-gray-700">
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class AdminNavComponent {
  constructor(public auth: AuthService) {}

  getUsername(): string {
    const userId = this.auth.getUserId();
    // Pour l'instant, on retourne l'ID utilisateur, mais on pourrait améliorer cela
    // en récupérant le nom d'utilisateur depuis le backend si nécessaire
    return userId || 'Utilisateur';
  }

  logout() {
    this.auth.logoutAndClear();
  }
}

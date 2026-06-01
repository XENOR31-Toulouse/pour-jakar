import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type Client = {
  id: string;
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
  createdAt: string;
};

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly baseUrl = '/api/site/clients';

  constructor(private http: HttpClient) {}

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.baseUrl);
  }

  getClientById(id: string): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/${id}`);
  }

  createClient(client: Omit<Client, 'id' | 'createdAt'>): Observable<Client> {
    return this.http.post<Client>(this.baseUrl, client);
  }

  updateClient(id: string, client: Partial<Client>): Observable<Client> {
    return this.http.put<Client>(`${this.baseUrl}/${id}`, client);
  }

  deleteClient(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

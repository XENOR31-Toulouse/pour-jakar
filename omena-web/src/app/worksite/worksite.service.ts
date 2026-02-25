import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type Worksite = { id: string; name: string; address?: string | null };
export type Assignment = { userId: string; assignedAt: string };

@Injectable({ providedIn: 'root' })
export class WorksiteService {
  private readonly baseUrl = '/api/site';

  constructor(private http: HttpClient) {}

  adminListWorksites() {
    return this.http.get<Worksite[]>(`${this.baseUrl}/admin/worksites`);
  }

  adminCreateWorksite(name: string, address: string) {
    return this.http.post<{ id: string }>(`${this.baseUrl}/admin/worksites`, { name, address });
  }

  adminDeleteWorksite(id: string) {
    return this.http.delete(`${this.baseUrl}/admin/worksites/${id}`);
  }

  adminAssign(worksiteId: string, userId: string) {
    return this.http.post(`${this.baseUrl}/admin/worksites/${worksiteId}/assign/${userId}`, {});
  }

  adminUnassign(worksiteId: string, userId: string) {
    return this.http.delete(`${this.baseUrl}/admin/worksites/${worksiteId}/assign/${userId}`);
  }

  adminListAssignments(worksiteId: string) {
  return this.http.get<Assignment[]>(`${this.baseUrl}/admin/worksites/${worksiteId}/assignments`);
}


}

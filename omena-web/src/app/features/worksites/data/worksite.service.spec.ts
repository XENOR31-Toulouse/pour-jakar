import { TestBed } from '@angular/core/testing';
import { WorksiteService } from './worksite.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

describe('WorksiteService', () => {
  let service: WorksiteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WorksiteService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(WorksiteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('adminListWorksites GET /api/site/admin/worksites', () => {
    service.adminListWorksites().subscribe();

    const req = httpMock.expectOne('/api/site/admin/worksites');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('adminCreateWorksite POST /api/site/admin/worksites with body', () => {
    service.adminCreateWorksite('test2', '3 rue').subscribe();

    const req = httpMock.expectOne('/api/site/admin/worksites');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'test2', address: '3 rue' });
    req.flush({ id: '1' });
  });

  it('adminAssign POST /api/site/admin/worksites/{worksiteId}/assign/{userId}', () => {
    service.adminAssign('w1', 'u1').subscribe();

    const req = httpMock.expectOne('/api/site/admin/worksites/w1/assign/u1');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush({});
  });

  it('adminListAssignments GET /api/site/admin/worksites/{id}/assignments', () => {
    service.adminListAssignments('w1').subscribe();

    const req = httpMock.expectOne('/api/site/admin/worksites/w1/assignments');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('myWorksites GET /api/site/api/my-worksites', () => {
    service.myWorksites().subscribe();

    const req = httpMock.expectOne('/api/site/api/my-worksites');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('addProgress POST /api/site/api/worksites/{id}/progress with note+percent', () => {
    service.addProgress('w1', 'note', 50).subscribe();

    const req = httpMock.expectOne('/api/site/api/worksites/w1/progress');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ note: 'note', percent: 50 });
    req.flush({});
  });
});

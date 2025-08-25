import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalysisHistoryService } from './analysis-history.service';
import { AnalysisHistoryResponse } from '../models/analysis-history';
import { environment } from '../../environments/environment';

describe('AnalysisHistoryService', () => {
  let service: AnalysisHistoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AnalysisHistoryService]
    });
    service = TestBed.inject(AnalysisHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAnalysisHistory', () => {
    it('should make GET request with default parameters', () => {
      const mockResponse: AnalysisHistoryResponse = {
        content: [],
        pageable: {
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 0,
          pageSize: 20,
          pageNumber: 0,
          paged: true,
          unpaged: false
        },
        last: true,
        totalPages: 0,
        totalElements: 0,
        size: 20,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        first: true,
        numberOfElements: 0,
        empty: true
      };

      service.getAnalysisHistory().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=0&size=20`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should make GET request with custom parameters', () => {
      const mockResponse: AnalysisHistoryResponse = {
        content: [],
        pageable: {
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 20,
          pageSize: 10,
          pageNumber: 2,
          paged: true,
          unpaged: false
        },
        last: false,
        totalPages: 5,
        totalElements: 50,
        size: 10,
        number: 2,
        sort: { empty: true, sorted: false, unsorted: true },
        first: false,
        numberOfElements: 10,
        empty: false
      };

      service.getAnalysisHistory(2, 10).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=2&size=10`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle HTTP error responses', () => {
      const errorMessage = 'Server error';
      
      service.getAnalysisHistory().subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=0&size=20`);
      req.flush(errorMessage, { status: 500, statusText: 'Internal Server Error' });
    });

    it('should handle network errors', () => {
      service.getAnalysisHistory().subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.error).toBeInstanceOf(ProgressEvent);
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=0&size=20`);
      req.error(new ProgressEvent('Network error'));
    });

    it('should handle large page numbers', () => {
      const mockResponse: AnalysisHistoryResponse = {
        content: [],
        pageable: {
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 990,
          pageSize: 10,
          pageNumber: 99,
          paged: true,
          unpaged: false
        },
        last: true,
        totalPages: 100,
        totalElements: 1000,
        size: 10,
        number: 99,
        sort: { empty: true, sorted: false, unsorted: true },
        first: false,
        numberOfElements: 0,
        empty: true
      };

      service.getAnalysisHistory(99, 10).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=99&size=10`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle zero page size', () => {
      const mockResponse: AnalysisHistoryResponse = {
        content: [],
        pageable: {
          sort: { empty: true, sorted: false, unsorted: true },
          offset: 0,
          pageSize: 20,
          pageNumber: 0,
          paged: true,
          unpaged: false
        },
        last: false,
        totalPages: 2,
        totalElements: 25,
        size: 20,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        first: false,
        numberOfElements: 10,
        empty: false
      };

      service.getAnalysisHistory(0, 0).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=0&size=0`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('deleteAllHistory', () => {
    it('should make DELETE request to correct endpoint', () => {
      const mockResponse = { message: 'All history deleted successfully' };

      service.deleteAllHistory().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history`);
      expect(req.request.method).toBe('DELETE');
      req.flush(mockResponse);
    });

    it('should handle DELETE success response', () => {
      const mockResponse = { message: 'Deleted 42 analysis results' };

      service.deleteAllHistory().subscribe(response => {
        expect(response.message).toContain('42');
        expect(response.message).toContain('Deleted');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history`);
      req.flush(mockResponse);
    });

    it('should handle DELETE error responses', () => {
      const errorMessage = 'Unauthorized';
      
      service.deleteAllHistory().subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(401);
          expect(error.statusText).toBe('Unauthorized');
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history`);
      req.flush(errorMessage, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle server errors during deletion', () => {
      service.deleteAllHistory().subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history`);
      req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should handle empty database deletion', () => {
      const mockResponse = { message: 'No history to delete' };

      service.deleteAllHistory().subscribe(response => {
        expect(response.message).toBe('No history to delete');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/history`);
      req.flush(mockResponse);
    });
  });

  describe('service configuration', () => {
    it('should use correct base URL from environment', () => {
      expect(service['baseUrl']).toBe(environment.apiUrl);
    });

    it('should be provided in root', () => {
      const injector = TestBed.inject(AnalysisHistoryService);
      expect(injector).toBeTruthy();
    });
  });

  describe('HTTP parameter handling', () => {
    it('should convert numbers to strings in query parameters', () => {
      service.getAnalysisHistory(5, 25).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=5&size=25`);
      expect(req.request.params.get('page')).toBe('5');
      expect(req.request.params.get('size')).toBe('25');
      req.flush({ content: [], totalElements: 0, totalPages: 0, size: 25, number: 5, first: false, last: true, pageable: { sort: { empty: true, sorted: false, unsorted: true }, offset: 125, pageSize: 25, pageNumber: 5, paged: true, unpaged: false }, sort: { empty: true, sorted: false, unsorted: true }, numberOfElements: 0, empty: true });
    });

    it('should handle negative parameters gracefully', () => {
      service.getAnalysisHistory(-1, -5).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/history?page=-1&size=-5`);
      expect(req.request.params.get('page')).toBe('-1');
      expect(req.request.params.get('size')).toBe('-5');
      req.flush({ content: [], totalElements: 0, totalPages: 0, size: 0, number: 0, first: true, last: true, pageable: { sort: { empty: true, sorted: false, unsorted: true }, offset: 0, pageSize: 0, pageNumber: 0, paged: true, unpaged: false }, sort: { empty: true, sorted: false, unsorted: true }, numberOfElements: 0, empty: true });
    });
  });
});

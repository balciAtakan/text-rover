import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalysisHistoryResponse } from '../models/analysis-history';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AnalysisHistoryService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Get paginated analysis history from the backend
   */
  getAnalysisHistory(page: number = 0, size: number = 20): Observable<AnalysisHistoryResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<AnalysisHistoryResponse>(`${this.baseUrl}/history`, { params });
  }

  /**
   * Delete all analysis history from the backend
   */
  deleteAllHistory(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/history`);
  }
}

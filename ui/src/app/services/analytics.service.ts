import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserAnalytics } from '../models/analytics.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAnalytics(userId: string, refresh: boolean = false): Observable<UserAnalytics> {
    const url = `${this.apiUrl}/analytics/user/${userId}${refresh ? '?refresh=true' : ''}`;
    return this.http.get<UserAnalytics>(url);
  }

  refreshAnalytics(userId: string): Observable<UserAnalytics> {
    return this.http.post<UserAnalytics>(`${this.apiUrl}/analytics/user/${userId}/refresh`, {});
  }
}

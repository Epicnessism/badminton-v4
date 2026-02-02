import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateStringingRequest, Stringing } from '../models/stringing.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class StringingService {
  private apiUrl = `${environment.apiUrl}/stringing`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getStringingsByStringer(userId: string): Observable<Stringing[]> {
    return this.http.get<Stringing[]>(`${this.apiUrl}/stringer/${userId}`, {
      headers: this.getHeaders()
    });
  }

  getStringingsByOwner(userId: string): Observable<Stringing[]> {
    return this.http.get<Stringing[]>(`${this.apiUrl}/owner/${userId}`, {
      headers: this.getHeaders()
    });
  }

  createStringing(request: CreateStringingRequest): Observable<Stringing> {
    return this.http.post<Stringing>(this.apiUrl, request, {
      headers: this.getHeaders()
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, AuthUser } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';

  private currentUserSubject = new BehaviorSubject<AuthUser | null>(this.getStoredUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        this.storeToken(response.token);
        const user: AuthUser = {
          userId: response.userId,
          username: response.username,
          givenName: response.givenName,
          familyName: response.familyName
        };
        this.storeUser(user);
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUser(): AuthUser | null {
    return this.currentUserSubject.value;
  }

  private storeToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private storeUser(user: AuthUser): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  private getStoredUser(): AuthUser | null {
    const userJson = localStorage.getItem(this.userKey);
    return userJson ? JSON.parse(userJson) : null;
  }
}

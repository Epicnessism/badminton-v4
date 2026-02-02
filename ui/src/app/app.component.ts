import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './services/auth.service';
import { AuthUser } from './models/auth.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, MatButtonModule, MatIconModule],
  template: `
    <mat-toolbar color="primary" class="app-toolbar">
      <span class="app-title">BadmintonTracker</span>
      <span class="toolbar-spacer"></span>
      <ng-container *ngIf="isLoggedIn()">
        <button mat-button (click)="goToCreateStringing()">Create Stringing</button>
        <span class="toolbar-user">{{ currentUser?.givenName }} {{ currentUser?.familyName }}</span>
        <button mat-icon-button (click)="logout()" aria-label="Logout">
          <mat-icon>logout</mat-icon>
        </button>
      </ng-container>
    </mat-toolbar>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .app-toolbar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
    }
    .app-title {
      font-weight: 600;
      font-size: 1.25rem;
    }
    .toolbar-spacer {
      flex: 1 1 auto;
    }
    .toolbar-user {
      margin: 0 0.5rem;
      font-weight: 500;
    }
    .main-content {
      padding-top: 64px;
    }
  `]
})
export class AppComponent {
  title = 'Badminton Stringing Tracker';
  currentUser: AuthUser | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  goToCreateStringing(): void {
    this.router.navigate(['/stringing/new']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

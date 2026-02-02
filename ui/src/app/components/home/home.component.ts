import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { StringingService } from '../../services/stringing.service';
import { Stringing } from '../../models/stringing.model';
import { AuthUser } from '../../models/auth.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  currentUser: AuthUser | null = null;
  stringings: Stringing[] = [];
  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private stringingService: StringingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadStringings();
  }

  loadStringings(): void {
    if (!this.currentUser) return;

    const userId = this.currentUser.userId;

    forkJoin({
      asStringer: this.stringingService.getStringingsByStringer(userId),
      asOwner: this.stringingService.getStringingsByOwner(userId)
    }).subscribe({
      next: (results) => {
        const allStringings = [...results.asStringer, ...results.asOwner];
        const uniqueStringings = allStringings.filter((stringing, index, self) =>
          index === self.findIndex(s => s.stringingId === stringing.stringingId)
        );
        this.stringings = uniqueStringings.sort((a, b) =>
          new Date(b.requestedAt).getTime() - new Date(a.requestedAt).getTime()
        );
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load stringings:', error);
        this.errorMessage = 'Failed to load stringings. Please try again.';
        this.isLoading = false;
      }
    });
  }

  getStateDisplayName(state: string): string {
    const stateMap: { [key: string]: string } = {
      'REQUESTED_BUT_NOT_DELIVERED': 'Requested',
      'RECEIVED_BUT_NOT_STARTED': 'Received',
      'IN_PROGRESS': 'In Progress',
      'FINISHED_BUT_NOT_PICKED_UP': 'Ready for Pickup',
      'FAILED_BUT_NOT_PICKED_UP': 'Failed - Pending Pickup',
      'COMPLETED': 'Completed',
      'FAILED_COMPLETED': 'Failed'
    };
    return stateMap[state] || state;
  }

  getStateClass(state: string): string {
    const classMap: { [key: string]: string } = {
      'REQUESTED_BUT_NOT_DELIVERED': 'state-pending',
      'RECEIVED_BUT_NOT_STARTED': 'state-pending',
      'IN_PROGRESS': 'state-progress',
      'FINISHED_BUT_NOT_PICKED_UP': 'state-ready',
      'FAILED_BUT_NOT_PICKED_UP': 'state-failed',
      'COMPLETED': 'state-completed',
      'FAILED_COMPLETED': 'state-failed'
    };
    return classMap[state] || '';
  }
}

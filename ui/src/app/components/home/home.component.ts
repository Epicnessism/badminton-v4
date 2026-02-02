import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';
import { StringingService } from '../../services/stringing.service';
import { Stringing, StringingState } from '../../models/stringing.model';
import { AuthUser } from '../../models/auth.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, MatSelectModule, MatFormFieldModule, MatButtonModule],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  currentUser: AuthUser | null = null;
  stringings: Stringing[] = [];
  isLoading = true;
  errorMessage: string | null = null;
  sortBy: 'date' | 'status' = 'date';

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
        this.stringings = uniqueStringings;
        this.applySorting();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load stringings:', error);
        this.errorMessage = 'Failed to load stringings. Please try again.';
        this.isLoading = false;
      }
    });
  }

  applySorting(): void {
    if (this.sortBy === 'date') {
      this.stringings.sort((a, b) =>
        new Date(b.requestedAt).getTime() - new Date(a.requestedAt).getTime()
      );
    } else if (this.sortBy === 'status') {
      const statusOrder: { [key: string]: number } = {
        'REQUESTED_BUT_NOT_DELIVERED': 1,
        'RECEIVED_BUT_NOT_STARTED': 2,
        'IN_PROGRESS': 3,
        'FINISHED_BUT_NOT_PICKED_UP': 4,
        'FAILED_BUT_NOT_PICKED_UP': 5,
        'COMPLETED': 6,
        'DECLINED': 7,
        'FAILED_COMPLETED': 8
      };
      this.stringings.sort((a, b) =>
        (statusOrder[a.state] || 99) - (statusOrder[b.state] || 99)
      );
    }
  }

  onSortChange(): void {
    this.applySorting();
  }

  isStringer(stringing: Stringing): boolean {
    return this.currentUser?.userId === stringing.stringerUserId;
  }

  canAcceptOrDecline(stringing: Stringing): boolean {
    return this.isStringer(stringing) && stringing.state === 'REQUESTED_BUT_NOT_DELIVERED';
  }

  acceptStringing(stringing: Stringing): void {
    this.stringingService.updateStringing(stringing.stringingId, {
      state: 'RECEIVED_BUT_NOT_STARTED'
    }).subscribe({
      next: (updated) => {
        const index = this.stringings.findIndex(s => s.stringingId === stringing.stringingId);
        if (index !== -1) {
          this.stringings[index] = updated;
        }
      },
      error: (error) => {
        console.error('Failed to accept stringing:', error);
        this.errorMessage = 'Failed to accept stringing. Please try again.';
      }
    });
  }

  declineStringing(stringing: Stringing): void {
    this.stringingService.updateStringing(stringing.stringingId, {
      state: 'DECLINED'
    }).subscribe({
      next: (updated) => {
        const index = this.stringings.findIndex(s => s.stringingId === stringing.stringingId);
        if (index !== -1) {
          this.stringings[index] = updated;
        }
      },
      error: (error) => {
        console.error('Failed to decline stringing:', error);
        this.errorMessage = 'Failed to decline stringing. Please try again.';
      }
    });
  }

  getStateDisplayName(state: string): string {
    const stateMap: { [key: string]: string } = {
      'REQUESTED_BUT_NOT_DELIVERED': 'Requested',
      'DECLINED': 'Declined',
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
      'DECLINED': 'state-declined',
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

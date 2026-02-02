import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../services/auth.service';
import { StringingService } from '../../services/stringing.service';
import { UserService } from '../../services/user.service';
import { Stringing, StringingState, ALL_STRINGING_STATES, STATE_TRANSITIONS, UpdateStringingRequest } from '../../models/stringing.model';
import { User } from '../../models/user.model';
import { AuthUser } from '../../models/auth.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, MatSelectModule, MatFormFieldModule, MatButtonModule, MatIconModule, MatChipsModule, MatInputModule, MatTooltipModule],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  currentUser: AuthUser | null = null;
  stringings: Stringing[] = [];
  allStringings: Stringing[] = [];
  isLoading = true;
  errorMessage: string | null = null;
  sortBy: 'date' | 'status' = 'date';
  
  allStates = ALL_STRINGING_STATES;
  selectedStates: Set<StringingState> = new Set();
  
  editingStringing: Stringing | null = null;
  editFormData: UpdateStringingRequest = {};
  stringers: User[] = [];

  constructor(
    private authService: AuthService,
    private stringingService: StringingService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadStringings();
    this.loadStringers();
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
        this.allStringings = uniqueStringings;
        this.applyFiltersAndSorting();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load stringings:', error);
        this.errorMessage = 'Failed to load stringings. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadStringers(): void {
    this.userService.getStringers().subscribe({
      next: (stringers) => {
        this.stringers = stringers;
      },
      error: (error) => {
        console.error('Failed to load stringers:', error);
      }
    });
  }

  applyFiltersAndSorting(): void {
    let filtered = [...this.allStringings];
    
    if (this.selectedStates.size > 0) {
      filtered = filtered.filter(s => this.selectedStates.has(s.state));
    }
    
    if (this.sortBy === 'date') {
      filtered.sort((a, b) =>
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
        'CANCELED': 7,
        'DECLINED': 8,
        'FAILED_COMPLETED': 9
      };
      filtered.sort((a, b) =>
        (statusOrder[a.state] || 99) - (statusOrder[b.state] || 99)
      );
    }
    
    this.stringings = filtered;
  }

  onSortChange(): void {
    this.applyFiltersAndSorting();
  }

  toggleStateFilter(state: StringingState): void {
    if (this.selectedStates.has(state)) {
      this.selectedStates.delete(state);
    } else {
      this.selectedStates.add(state);
    }
    this.applyFiltersAndSorting();
  }

  isStateSelected(state: StringingState): boolean {
    return this.selectedStates.has(state);
  }

  isStringer(stringing: Stringing): boolean {
    return this.currentUser?.userId === stringing.stringerUserId;
  }

  isOwner(stringing: Stringing): boolean {
    return this.currentUser?.userId === stringing.ownerUserId;
  }

  canAcceptOrDecline(stringing: Stringing): boolean {
    return this.isStringer(stringing) && stringing.state === 'REQUESTED_BUT_NOT_DELIVERED';
  }

  getNextStates(stringing: Stringing): StringingState[] {
    if (!this.isStringer(stringing)) return [];
    const transitions = STATE_TRANSITIONS[stringing.state] || [];
    return transitions.filter(s => s !== 'DECLINED' && s !== 'CANCELED');
  }

  getNextStateButtonLabel(state: StringingState): string {
    const labels: { [key: string]: string } = {
      'RECEIVED_BUT_NOT_STARTED': 'Mark as Received',
      'IN_PROGRESS': 'Start Stringing',
      'FINISHED_BUT_NOT_PICKED_UP': 'Mark as Finished',
      'FAILED_BUT_NOT_PICKED_UP': 'Mark as Failed',
      'COMPLETED': 'Complete',
      'FAILED_COMPLETED': 'Mark Failed Complete'
    };
    return labels[state] || state;
  }

  proceedToState(stringing: Stringing, newState: StringingState): void {
    this.stringingService.updateStringing(stringing.stringingId, { state: newState }).subscribe({
      next: (updated) => {
        this.updateStringingInList(updated);
      },
      error: (error) => {
        console.error('Failed to update stringing:', error);
        this.errorMessage = 'Failed to update stringing. Please try again.';
      }
    });
  }

  acceptStringing(stringing: Stringing): void {
    this.proceedToState(stringing, 'RECEIVED_BUT_NOT_STARTED');
  }

  declineStringing(stringing: Stringing): void {
    this.stringingService.updateStringing(stringing.stringingId, { state: 'DECLINED' }).subscribe({
      next: (updated) => {
        this.updateStringingInList(updated);
      },
      error: (error) => {
        console.error('Failed to decline stringing:', error);
        this.errorMessage = 'Failed to decline stringing. Please try again.';
      }
    });
  }

  cancelStringing(stringing: Stringing): void {
    this.stringingService.updateStringing(stringing.stringingId, { state: 'CANCELED' }).subscribe({
      next: (updated) => {
        this.updateStringingInList(updated);
      },
      error: (error) => {
        console.error('Failed to cancel stringing:', error);
        this.errorMessage = 'Failed to cancel stringing. Please try again.';
      }
    });
  }

  canEdit(stringing: Stringing): boolean {
    return this.isOwner(stringing) && stringing.state === 'REQUESTED_BUT_NOT_DELIVERED';
  }

  canCancel(stringing: Stringing): boolean {
    return this.isOwner(stringing) && stringing.state === 'REQUESTED_BUT_NOT_DELIVERED';
  }

  startEditing(stringing: Stringing): void {
    this.editingStringing = stringing;
    this.editFormData = {
      racketMake: stringing.racketMake,
      racketModel: stringing.racketModel,
      stringType: stringing.stringType,
      stringColor: stringing.stringColor,
      mainsTensionLbs: stringing.mainsTensionLbs,
      crossesTensionLbs: stringing.crossesTensionLbs,
      stringerUserId: stringing.stringerUserId
    };
  }

  cancelEditing(): void {
    this.editingStringing = null;
    this.editFormData = {};
  }

  saveEditing(): void {
    if (!this.editingStringing) return;
    
    this.stringingService.updateStringing(this.editingStringing.stringingId, this.editFormData).subscribe({
      next: (updated) => {
        this.updateStringingInList(updated);
        this.cancelEditing();
      },
      error: (error) => {
        console.error('Failed to update stringing:', error);
        this.errorMessage = 'Failed to update stringing. Please try again.';
      }
    });
  }

  private updateStringingInList(updated: Stringing): void {
    const allIndex = this.allStringings.findIndex(s => s.stringingId === updated.stringingId);
    if (allIndex !== -1) {
      this.allStringings[allIndex] = updated;
    }
    this.applyFiltersAndSorting();
  }

  getStateDisplayName(state: string): string {
    const stateMap: { [key: string]: string } = {
      'REQUESTED_BUT_NOT_DELIVERED': 'Requested',
      'CANCELED': 'Canceled',
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
      'CANCELED': 'state-canceled',
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

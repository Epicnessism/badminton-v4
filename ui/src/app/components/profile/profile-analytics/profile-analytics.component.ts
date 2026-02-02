import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { UserAnalytics } from '../../../models/analytics.model';

@Component({
  selector: 'app-profile-analytics',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule],
  templateUrl: './profile-analytics.component.html'
})
export class ProfileAnalyticsComponent {
  @Input() analytics: UserAnalytics | null = null;
  @Input() isLoading = false;
  @Input() errorMessage: string | null = null;
  
  @Output() refresh = new EventEmitter<void>();
  @Output() retry = new EventEmitter<void>();

  onRefresh(): void {
    this.refresh.emit();
  }

  onRetry(): void {
    this.retry.emit();
  }

  formatAnalyticsDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString();
  }

  getTopItems(map: Record<string, number> | null, limit: number = 5): {key: string, value: number}[] {
    if (!map) return [];
    return Object.entries(map)
      .sort((a, b) => b[1] - a[1])
      .slice(0, limit)
      .map(([key, value]) => ({ key, value }));
  }

  getStateColor(state: string): string {
    const colors: Record<string, string> = {
      'REQUESTED_BUT_NOT_DELIVERED': '#2196F3',
      'CANCELED': '#9E9E9E',
      'DECLINED': '#F44336',
      'RECEIVED_BUT_NOT_STARTED': '#FF9800',
      'IN_PROGRESS': '#9C27B0',
      'FINISHED_BUT_NOT_PICKED_UP': '#4CAF50',
      'FAILED_BUT_NOT_PICKED_UP': '#E91E63',
      'COMPLETED': '#00BCD4',
      'FAILED_COMPLETED': '#795548'
    };
    return colors[state] || '#666';
  }

  formatStateName(state: string): string {
    return state.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  }
}

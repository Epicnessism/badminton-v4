import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { AnalyticsService } from '../../services/analytics.service';
import { AuthUser } from '../../models/auth.model';
import { UpdateUserRequest } from '../../models/user.model';
import { UserAnalytics } from '../../models/analytics.model';
import { ProfileInfoComponent } from './profile-info/profile-info.component';
import { ProfileAnalyticsComponent } from './profile-analytics/profile-analytics.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ProfileInfoComponent, ProfileAnalyticsComponent],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  @ViewChild(ProfileInfoComponent) profileInfoComponent!: ProfileInfoComponent;
  
  currentUser: AuthUser | null = null;
  isLoading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  // Analytics
  analytics: UserAnalytics | null = null;
  isLoadingAnalytics = false;
  analyticsError: string | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private analyticsService: AnalyticsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadAnalytics();
  }

  // Profile info handlers
  onProfileUpdated(updateRequest: UpdateUserRequest): void {
    if (!this.currentUser) return;
    
    this.isLoading = true;
    this.clearMessages();

    this.userService.updateUser(this.currentUser.userId, updateRequest).subscribe({
      next: (updatedUser) => {
        this.authService.updateCurrentUser(updatedUser);
        this.currentUser = this.authService.getCurrentUser();
        this.isLoading = false;
        this.successMessage = 'Profile updated successfully!';
        this.profileInfoComponent?.onProfileSaved();
      },
      error: (error) => {
        console.error('Failed to update profile:', error);
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        this.isLoading = false;
      }
    });
  }

  onPasswordUpdated(newPassword: string): void {
    if (!this.currentUser) return;

    this.isLoading = true;
    this.clearMessages();

    this.userService.updateUser(this.currentUser.userId, { password: newPassword }).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Password updated successfully!';
        this.profileInfoComponent?.onPasswordSaved();
      },
      error: (error) => {
        console.error('Failed to update password:', error);
        this.errorMessage = error.error?.message || 'Failed to update password. Please try again.';
        this.isLoading = false;
      }
    });
  }

  clearMessages(): void {
    this.errorMessage = null;
    this.successMessage = null;
  }

  // Analytics handlers
  loadAnalytics(forceRefresh: boolean = false): void {
    if (!this.currentUser) return;
    
    this.isLoadingAnalytics = true;
    this.analyticsError = null;

    this.analyticsService.getAnalytics(this.currentUser.userId, forceRefresh).subscribe({
      next: (analytics) => {
        this.analytics = analytics;
        this.isLoadingAnalytics = false;
      },
      error: (error) => {
        console.error('Failed to load analytics:', error);
        this.analyticsError = 'Failed to load analytics data.';
        this.isLoadingAnalytics = false;
      }
    });
  }

  refreshAnalytics(): void {
    if (!this.currentUser) return;
    
    this.isLoadingAnalytics = true;
    this.analyticsError = null;

    this.analyticsService.refreshAnalytics(this.currentUser.userId).subscribe({
      next: (analytics) => {
        this.analytics = analytics;
        this.isLoadingAnalytics = false;
      },
      error: (error) => {
        console.error('Failed to refresh analytics:', error);
        this.analyticsError = 'Failed to refresh analytics data.';
        this.isLoadingAnalytics = false;
      }
    });
  }
}

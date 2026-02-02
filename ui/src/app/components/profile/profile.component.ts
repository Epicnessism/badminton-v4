import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { AuthUser } from '../../models/auth.model';
import { UpdateUserRequest } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  currentUser: AuthUser | null = null;
  isEditing = false;
  isChangingPassword = false;
  isLoading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  editFormData: UpdateUserRequest = {};
  
  newPassword = '';
  confirmPassword = '';
  showNewPassword = false;
  showConfirmPassword = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }
  }

  startEditing(): void {
    if (!this.currentUser) return;
    this.isEditing = true;
    this.editFormData = {
      givenName: this.currentUser.givenName,
      familyName: this.currentUser.familyName,
      email: this.currentUser.email,
      birthday: this.currentUser.birthday
    };
    this.clearMessages();
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.editFormData = {};
    this.clearMessages();
  }

  saveProfile(): void {
    if (!this.currentUser) return;
    
    this.isLoading = true;
    this.clearMessages();

    this.userService.updateUser(this.currentUser.userId, this.editFormData).subscribe({
      next: (updatedUser) => {
        this.authService.updateCurrentUser(updatedUser);
        this.currentUser = this.authService.getCurrentUser();
        this.isEditing = false;
        this.isLoading = false;
        this.successMessage = 'Profile updated successfully!';
      },
      error: (error) => {
        console.error('Failed to update profile:', error);
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        this.isLoading = false;
      }
    });
  }

  startChangingPassword(): void {
    this.isChangingPassword = true;
    this.newPassword = '';
    this.confirmPassword = '';
    this.showNewPassword = false;
    this.showConfirmPassword = false;
    this.clearMessages();
  }

  cancelChangingPassword(): void {
    this.isChangingPassword = false;
    this.newPassword = '';
    this.confirmPassword = '';
    this.clearMessages();
  }

  savePassword(): void {
    if (!this.currentUser) return;
    
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters.';
      return;
    }

    this.isLoading = true;
    this.clearMessages();

    this.userService.updateUser(this.currentUser.userId, { password: this.newPassword }).subscribe({
      next: () => {
        this.isChangingPassword = false;
        this.newPassword = '';
        this.confirmPassword = '';
        this.isLoading = false;
        this.successMessage = 'Password updated successfully!';
      },
      error: (error) => {
        console.error('Failed to update password:', error);
        this.errorMessage = error.error?.message || 'Failed to update password. Please try again.';
        this.isLoading = false;
      }
    });
  }

  toggleShowNewPassword(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleShowConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  private clearMessages(): void {
    this.errorMessage = null;
    this.successMessage = null;
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}

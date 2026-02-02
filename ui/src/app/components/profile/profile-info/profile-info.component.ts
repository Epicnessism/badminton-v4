import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthUser } from '../../../models/auth.model';
import { UpdateUserRequest } from '../../../models/user.model';

@Component({
  selector: 'app-profile-info',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTooltipModule],
  templateUrl: './profile-info.component.html'
})
export class ProfileInfoComponent {
  @Input() currentUser: AuthUser | null = null;
  @Input() isLoading = false;
  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;
  
  @Output() profileUpdated = new EventEmitter<UpdateUserRequest>();
  @Output() passwordUpdated = new EventEmitter<string>();
  @Output() clearMessages = new EventEmitter<void>();

  isEditing = false;
  isChangingPassword = false;
  editFormData: UpdateUserRequest = {};
  
  newPassword = '';
  confirmPassword = '';
  showNewPassword = false;
  showConfirmPassword = false;

  startEditing(): void {
    if (!this.currentUser) return;
    this.isEditing = true;
    this.editFormData = {
      givenName: this.currentUser.givenName,
      familyName: this.currentUser.familyName,
      email: this.currentUser.email,
      birthday: this.currentUser.birthday
    };
    this.clearMessages.emit();
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.editFormData = {};
    this.clearMessages.emit();
  }

  saveProfile(): void {
    this.profileUpdated.emit(this.editFormData);
  }

  onProfileSaved(): void {
    this.isEditing = false;
    this.editFormData = {};
  }

  startChangingPassword(): void {
    this.isChangingPassword = true;
    this.newPassword = '';
    this.confirmPassword = '';
    this.showNewPassword = false;
    this.showConfirmPassword = false;
    this.clearMessages.emit();
  }

  cancelChangingPassword(): void {
    this.isChangingPassword = false;
    this.newPassword = '';
    this.confirmPassword = '';
    this.clearMessages.emit();
  }

  savePassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      return;
    }
    if (this.newPassword.length < 6) {
      return;
    }
    this.passwordUpdated.emit(this.newPassword);
  }

  onPasswordSaved(): void {
    this.isChangingPassword = false;
    this.newPassword = '';
    this.confirmPassword = '';
  }

  toggleShowNewPassword(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleShowConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { UserService } from '../../services/user.service';
import { CreateUserRequest, User } from '../../models/user.model';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCheckboxModule],
  templateUrl: './create-user.component.html'
})
export class CreateUserComponent {
  formData: CreateUserRequest = {
    givenName: '',
    familyName: '',
    email: '',
    username: '',
    birthday: '',
    password: '',
    isStringer: false
  };

  createdUser: User | null = null;
  errorMessage: string | null = null;
  isLoading = false;
  submitted = false;

  constructor(private userService: UserService) {}

  onSubmit(): void {
    this.submitted = true;
    
    if (!this.isFormValid()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.createdUser = null;

    this.userService.createUser(this.formData).subscribe({
      next: (user) => {
        this.createdUser = user;
        this.isLoading = false;
        this.resetForm();
      },
      error: (error) => {
        console.error('Error creating user:', error);
        this.errorMessage = error.error?.message || 'Failed to create user. Please try again.';
        this.isLoading = false;
      }
    });
  }

  isFormValid(): boolean {
    return !!(
      this.formData.givenName &&
      this.formData.familyName &&
      this.formData.email &&
      this.isValidEmail(this.formData.email) &&
      this.formData.username &&
      this.formData.birthday &&
      this.formData.password
    );
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  resetForm(): void {
    this.formData = {
      givenName: '',
      familyName: '',
      email: '',
      username: '',
      birthday: '',
      password: '',
      isStringer: false
    };
    this.submitted = false;
  }
}

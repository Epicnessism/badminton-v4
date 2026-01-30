import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { LoginRequest } from '../../models/auth.model';
import { CreateUserRequest } from '../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  activeTab: 'login' | 'register' = 'login';

  loginData: LoginRequest = {
    username: '',
    password: ''
  };

  registerData: CreateUserRequest = {
    givenName: '',
    familyName: '',
    email: '',
    username: '',
    age: 0,
    birthday: '',
    password: ''
  };

  errorMessage: string | null = null;
  isLoading = false;
  submitted = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  setActiveTab(tab: 'login' | 'register'): void {
    this.activeTab = tab;
    this.errorMessage = null;
    this.submitted = false;
  }

  onLogin(): void {
    this.submitted = true;
    if (!this.loginData.username || !this.loginData.password) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.authService.login(this.loginData).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        this.errorMessage = error.error || 'Invalid username or password';
        this.isLoading = false;
      }
    });
  }

  onRegister(): void {
    this.submitted = true;
    if (!this.isRegisterFormValid()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.userService.createUser(this.registerData).subscribe({
      next: (user) => {
        const loginRequest: LoginRequest = {
          username: this.registerData.username,
          password: this.registerData.password
        };
        this.authService.login(loginRequest).subscribe({
          next: () => {
            this.isLoading = false;
            this.router.navigate(['/home']);
          },
          error: (error) => {
            console.error('Auto-login after registration failed:', error);
            this.isLoading = false;
            this.setActiveTab('login');
            this.loginData.username = this.registerData.username;
          }
        });
      },
      error: (error) => {
        console.error('Registration failed:', error);
        this.errorMessage = error.error?.message || 'Failed to create account. Please try again.';
        this.isLoading = false;
      }
    });
  }

  isRegisterFormValid(): boolean {
    return !!(
      this.registerData.givenName &&
      this.registerData.familyName &&
      this.registerData.email &&
      this.isValidEmail(this.registerData.email) &&
      this.registerData.username &&
      this.registerData.age >= 0 &&
      this.registerData.birthday &&
      this.registerData.password
    );
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}

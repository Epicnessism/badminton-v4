import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { UserService } from '../../services/user.service';
import { StringingService } from '../../services/stringing.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { CreateStringingRequest } from '../../models/stringing.model';

@Component({
  selector: 'app-create-stringing',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatSelectModule, MatFormFieldModule, MatInputModule],
  templateUrl: './create-stringing.component.html'
})
export class CreateStringingComponent implements OnInit {
  users: User[] = [];
  stringers: User[] = [];
  ownerSelectMode: 'ME' | 'SEARCH' = 'ME';
  isLoading = false;
  errorMessage: string | null = null;
  submitted = false;

  formData: CreateStringingRequest = {
    stringerUserId: '',
    ownerUserId: '',
    racketMake: '',
    racketModel: '',
    stringType: '',
    stringColor: '',
    mainsTensionLbs: null,
    crossesTensionLbs: null
  };

  constructor(
    private userService: UserService,
    private stringingService: StringingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.setOwnerMe();
    this.loadStringers();
  }

  loadStringers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.userService.getStringers().subscribe({
      next: (users) => {
        this.stringers = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load stringers:', error);
        this.errorMessage = 'Failed to load stringers. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage = 'Failed to load users. Please try again.';
        this.isLoading = false;
      }
    });
  }

  setOwnerMe(): void {
    this.ownerSelectMode = 'ME';
    const currentUser = this.authService.getCurrentUser();

    if (!currentUser) {
      this.formData.ownerUserId = '';
      return;
    }

    this.formData.ownerUserId = currentUser.userId;
  }

  getOwnerDisplayName(): string {
    if (this.ownerSelectMode === 'ME') {
      const currentUser = this.authService.getCurrentUser();
      return currentUser ? `${currentUser.givenName} ${currentUser.familyName}`.trim() : '';
    }
    const owner = this.users.find(u => u.userId === this.formData.ownerUserId);
    return owner ? `${owner.givenName} ${owner.familyName}`.trim() : '';
  }

  enableOwnerSearch(): void {
    this.ownerSelectMode = 'SEARCH';
    if (this.users.length === 0) {
      this.loadUsers();
    }
  }

  onOwnerChanged(): void {
  }

  isFormValid(): boolean {
    return !!(
      this.formData.stringerUserId &&
      this.formData.ownerUserId &&
      this.formData.racketMake &&
      this.formData.racketModel &&
      this.formData.mainsTensionLbs !== null &&
      this.formData.mainsTensionLbs > 0 &&
      this.formData.crossesTensionLbs !== null &&
      this.formData.crossesTensionLbs > 0
    );
  }

  onSubmit(): void {
    this.submitted = true;

    if (!this.isFormValid()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.stringingService.createStringing(this.formData).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Failed to create stringing:', error);
        this.errorMessage = error.error?.message || 'Failed to create stringing. Please try again.';
        this.isLoading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/home']);
  }
}

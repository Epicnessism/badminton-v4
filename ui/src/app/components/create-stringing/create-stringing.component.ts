import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule, MatAutocomplete } from '@angular/material/autocomplete';
import { MatSliderModule } from '@angular/material/slider';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Observable, map, startWith, BehaviorSubject, combineLatest } from 'rxjs';
import { UserService } from '../../services/user.service';
import { StringingService } from '../../services/stringing.service';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { CreateStringingRequest } from '../../models/stringing.model';
import { 
  RACKET_MAKES, RACKET_MODELS, STRING_TYPES, STRING_COLORS,
  TENSION_MIN, TENSION_MAX, TENSION_DEFAULT, TENSION_PRESETS
} from '../../data/stringing-options.data';

@Component({
  selector: 'app-create-stringing',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, 
    MatButtonModule, MatSelectModule, MatFormFieldModule, MatInputModule,
    MatAutocompleteModule, MatSliderModule, MatChipsModule, MatIconModule, MatTooltipModule
  ],
  templateUrl: './create-stringing.component.html'
})
export class CreateStringingComponent implements OnInit {
  users: User[] = [];
  stringers: User[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  submitted = false;
  currentUserId: string = '';

  // Autocomplete controls - for owner/stringer we store the search text, not userId
  ownerSearchControl = new FormControl('');
  stringerSearchControl = new FormControl('');
  makeControl = new FormControl('');
  modelControl = new FormControl('');
  stringTypeControl = new FormControl('');
  stringColorControl = new FormControl('');

  // Validation flags
  ownerInvalid = false;
  stringerInvalid = false;

  // Track selected tension preset (null means custom/manual)
  selectedPresetIndex: number | null = null;

  // Filtered options for autocomplete
  filteredOwners$!: Observable<User[]>;
  filteredStringers$!: Observable<User[]>;
  filteredMakes$!: Observable<string[]>;
  filteredModels$!: Observable<string[]>;
  filteredStringTypes$!: Observable<string[]>;
  filteredColors$!: Observable<string[]>;

  // BehaviorSubject to trigger model list updates
  private modelsSubject = new BehaviorSubject<string[]>([]);

  // Static data
  allMakes = RACKET_MAKES;
  allModels: string[] = [];
  allStringTypes = STRING_TYPES;
  allColors = STRING_COLORS;
  tensionPresets = TENSION_PRESETS;
  tensionMin = TENSION_MIN;
  tensionMax = TENSION_MAX;

  formData: CreateStringingRequest = {
    stringerUserId: '',
    ownerUserId: '',
    racketMake: '',
    racketModel: '',
    stringType: '',
    stringColor: '',
    mainsTensionLbs: TENSION_DEFAULT,
    crossesTensionLbs: TENSION_DEFAULT
  };

  constructor(
    private userService: UserService,
    private stringingService: StringingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadStringers();
    this.setupAutocomplete();
  }

  setupAutocomplete(): void {
    // Filter owners (users) - always show all when empty, filter when typing
    this.filteredOwners$ = this.ownerSearchControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const searchValue = value || '';
        if (!searchValue) return this.users;
        return this.filterUsers(searchValue, this.users);
      })
    );

    // Filter stringers - always show all when empty, filter when typing
    this.filteredStringers$ = this.stringerSearchControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const searchValue = value || '';
        if (!searchValue) return this.stringers;
        return this.filterUsers(searchValue, this.stringers);
      })
    );

    // When owner search text changes and differs from selected user's name, clear selection
    this.ownerSearchControl.valueChanges.subscribe(value => {
      if (this.formData.ownerUserId) {
        const selectedUser = this.users.find(u => u.userId === this.formData.ownerUserId);
        if (selectedUser) {
          const expectedName = this.getUserDisplayName(selectedUser);
          if (value !== expectedName) {
            // User is typing something different - clear selection
            this.formData.ownerUserId = '';
          }
        }
      }
      // Validate - check if typed value matches any user
      if (value && !this.formData.ownerUserId) {
        const matches = this.filterUsers(value, this.users);
        this.ownerInvalid = matches.length === 0;
      } else {
        this.ownerInvalid = false;
      }
    });

    // When stringer search text changes and differs from selected user's name, clear selection
    this.stringerSearchControl.valueChanges.subscribe(value => {
      if (this.formData.stringerUserId) {
        const selectedUser = this.stringers.find(u => u.userId === this.formData.stringerUserId);
        if (selectedUser) {
          const expectedName = this.getUserDisplayName(selectedUser);
          if (value !== expectedName) {
            this.formData.stringerUserId = '';
          }
        }
      }
      if (value && !this.formData.stringerUserId) {
        const matches = this.filterUsers(value, this.stringers);
        this.stringerInvalid = matches.length === 0;
      } else {
        this.stringerInvalid = false;
      }
    });

    // Filter makes
    this.filteredMakes$ = this.makeControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterOptions(value || '', this.allMakes))
    );

    // Filter models - combine model input changes with model list updates
    this.filteredModels$ = combineLatest([
      this.modelControl.valueChanges.pipe(startWith('')),
      this.modelsSubject
    ]).pipe(
      map(([value, models]) => this.filterOptions(value || '', models))
    );

    // Filter string types
    this.filteredStringTypes$ = this.stringTypeControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterOptions(value || '', this.allStringTypes))
    );

    // Filter colors
    this.filteredColors$ = this.stringColorControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterOptions(value || '', this.allColors))
    );

    // Sync autocomplete values with formData
    this.makeControl.valueChanges.subscribe(value => {
      this.formData.racketMake = value || '';
      this.updateModelsForMake(value || '');
    });
    this.modelControl.valueChanges.subscribe(value => this.formData.racketModel = value || '');
    this.stringTypeControl.valueChanges.subscribe(value => this.formData.stringType = value || '');
    this.stringColorControl.valueChanges.subscribe(value => this.formData.stringColor = value || '');
  }

  filterOptions(value: string, options: string[]): string[] {
    const filterValue = value.toLowerCase();
    return options.filter(option => option.toLowerCase().includes(filterValue));
  }

  filterUsers(value: string, users: User[]): User[] {
    const filterValue = value.toLowerCase();
    return users.filter(user => {
      const fullName = `${user.givenName} ${user.familyName}`.toLowerCase();
      return fullName.includes(filterValue);
    });
  }

  updateModelsForMake(make: string): void {
    this.allModels = RACKET_MODELS[make] || [];
    // Also include models from 'Other' category
    if (RACKET_MODELS['Other']) {
      this.allModels = [...this.allModels, ...RACKET_MODELS['Other']];
    }
    // Emit updated models to trigger filtered observable
    this.modelsSubject.next(this.allModels);
  }

  applyTensionPreset(preset: { mains: number; crosses: number }, index: number): void {
    this.formData.mainsTensionLbs = preset.mains;
    this.formData.crossesTensionLbs = preset.crosses;
    this.selectedPresetIndex = index;
  }

  onMainsTensionChange(): void {
    // User manually changed tension - unselect any preset
    this.selectedPresetIndex = null;
    // Ensure crosses tension is never lower than mains
    if (this.formData.crossesTensionLbs !== null && 
        this.formData.mainsTensionLbs !== null &&
        this.formData.crossesTensionLbs < this.formData.mainsTensionLbs) {
      this.formData.crossesTensionLbs = this.formData.mainsTensionLbs;
    }
  }

  onCrossesTensionChange(): void {
    // User manually changed tension - unselect any preset
    this.selectedPresetIndex = null;
    // Ensure crosses tension is never lower than mains
    if (this.formData.crossesTensionLbs !== null && 
        this.formData.mainsTensionLbs !== null &&
        this.formData.crossesTensionLbs < this.formData.mainsTensionLbs) {
      this.formData.crossesTensionLbs = this.formData.mainsTensionLbs;
    }
  }

  // Handle enter key to select first matching option
  onEnterSelect(control: FormControl, options: string[], currentValue: string): void {
    const filtered = this.filterOptions(currentValue || '', options);
    if (filtered.length > 0) {
      control.setValue(filtered[0]);
    }
  }

  // Get display name for user with (me) indicator
  getUserDisplayName(user: User): string {
    const isMe = user.userId === this.currentUserId;
    return `${user.givenName} ${user.familyName}${isMe ? ' (me)' : ''}`;
  }

  onOwnerSelected(user: User): void {
    this.formData.ownerUserId = user.userId;
    this.ownerSearchControl.setValue(this.getUserDisplayName(user));
    this.ownerInvalid = false;
  }

  onStringerSelected(user: User): void {
    this.formData.stringerUserId = user.userId;
    this.stringerSearchControl.setValue(this.getUserDisplayName(user));
    this.stringerInvalid = false;
  }

  clearOwner(): void {
    this.formData.ownerUserId = '';
    this.ownerSearchControl.setValue('');
    this.ownerInvalid = false;
  }

  clearStringer(): void {
    this.formData.stringerUserId = '';
    this.stringerSearchControl.setValue('');
    this.stringerInvalid = false;
  }

  // Trigger filter refresh on focus to show dropdown
  onOwnerFocus(): void {
    // Re-emit current value to trigger filter and show dropdown
    this.ownerSearchControl.setValue(this.ownerSearchControl.value || '');
  }

  onStringerFocus(): void {
    this.stringerSearchControl.setValue(this.stringerSearchControl.value || '');
  }

  onOwnerBlur(): void {
    // If user typed something but didn't select a valid user, clear input and show error
    const value = this.ownerSearchControl.value || '';
    if (value && !this.formData.ownerUserId) {
      // No valid selection - clear the input and mark as invalid
      this.ownerSearchControl.setValue('');
      this.ownerInvalid = true;
    }
  }

  onStringerBlur(): void {
    const value = this.stringerSearchControl.value || '';
    if (value && !this.formData.stringerUserId) {
      // No valid selection - clear the input and mark as invalid
      this.stringerSearchControl.setValue('');
      this.stringerInvalid = true;
    }
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

    const currentUser = this.authService.getCurrentUser();
    this.currentUserId = currentUser?.userId || '';

    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
        // Pre-fill with current user
        if (currentUser) {
          const currentUserInList = this.users.find(u => u.userId === currentUser.userId);
          if (currentUserInList) {
            this.formData.ownerUserId = currentUser.userId;
            this.ownerSearchControl.setValue(this.getUserDisplayName(currentUserInList));
          }
        }
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.errorMessage = 'Failed to load users. Please try again.';
        this.isLoading = false;
      }
    });
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

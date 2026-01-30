import { Routes } from '@angular/router';
import { CreateUserComponent } from './components/create-user/create-user.component';

export const routes: Routes = [
  { path: '', component: CreateUserComponent },
  { path: '**', redirectTo: '' }
];

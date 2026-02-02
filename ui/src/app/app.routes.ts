import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { CreateStringingComponent } from './components/create-stringing/create-stringing.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard, loginGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'stringing/new', component: CreateStringingComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '/home' }
];

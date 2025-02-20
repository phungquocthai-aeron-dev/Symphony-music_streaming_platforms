import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/auth/login/login.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginGuard } from './core/guards/login.guard';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { 
        path: 'home',
        component: MainLayoutComponent,
        children: [
            {path: '', component: HomeComponent}
        ]
     },
    { path: 'login', component: LoginComponent, canActivate: [LoginGuard]},
    { path: 'register', component: RegisterComponent },
];

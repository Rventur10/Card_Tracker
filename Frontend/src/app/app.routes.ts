import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';  // ← Add this import

export const routes: Routes = [
    { path: 'dashboard', component: DashboardComponent },  
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' }  
];
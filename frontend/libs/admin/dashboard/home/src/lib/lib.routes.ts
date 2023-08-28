import { Route } from '@angular/router';
import { AdminDashboardHomeComponent } from './admin-dashboard-home/admin-dashboard-home.component';

export const adminDashboardHomeRoutes: Route[] = [
  { path: '', pathMatch: 'full', component: AdminDashboardHomeComponent }
];

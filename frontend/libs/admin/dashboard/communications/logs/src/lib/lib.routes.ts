import { Route } from '@angular/router';
import { AdminDashboardCommunicationsLogsPageComponent } from './admin-dashboard-communications-logs-page/admin-dashboard-communications-logs-page.component';

export const adminDashboardCommunicationsLogsRoutes: Route[] = [
  { path: '', pathMatch: 'full', component: AdminDashboardCommunicationsLogsPageComponent }
];

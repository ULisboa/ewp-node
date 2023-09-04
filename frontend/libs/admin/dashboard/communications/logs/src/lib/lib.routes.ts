import { Route } from '@angular/router';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications-logs-page/communications-logs-page.component';

export const adminDashboardCommunicationsLogsRoutes: Route[] = [
  { path: '', pathMatch: 'full', component: AdminDashboardCommunicationsLogsPageComponent }
];

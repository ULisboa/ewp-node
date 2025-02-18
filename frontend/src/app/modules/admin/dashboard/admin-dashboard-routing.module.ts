import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications/logs/pages/communications-logs-page/communications-logs-page.component';

const routes: Routes = [
  {
    path: '**',
    component: AdminDashboardCommunicationsLogsPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminDashboardRoutingModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardCommunicationsLogsPageComponent } from './admin-dashboard-communications-logs-page/admin-dashboard-communications-logs-page.component';
import { adminDashboardCommunicationsLogsRoutes } from './lib.routes';
import { RouterModule } from '@angular/router';
import { MultiSelectModule } from 'primeng/multiselect';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { MessagesModule } from 'primeng/messages';

@NgModule({
  imports: [
    MessagesModule,
    MultiSelectModule,
    TableModule,

    CommonModule, 
    FormsModule,
    RouterModule.forChild(adminDashboardCommunicationsLogsRoutes)
  ],
  declarations: [
    AdminDashboardCommunicationsLogsPageComponent
  ],
})
export class AdminDashboardCommunicationsLogsModule {}

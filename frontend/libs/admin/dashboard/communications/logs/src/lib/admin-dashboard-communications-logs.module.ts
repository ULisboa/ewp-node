import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardCommunicationsLogsPageComponent } from './admin-dashboard-communications-logs-page/admin-dashboard-communications-logs-page.component';
import { adminDashboardCommunicationsLogsRoutes } from './lib.routes';
import { RouterModule } from '@angular/router';
import { AccordionModule } from 'primeng/accordion';
import { MultiSelectModule } from 'primeng/multiselect';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { MessagesModule } from 'primeng/messages';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AdminDashboardCommunicationLogDetailComponent } from './admin-dashboard-communication-log-detail/admin-dashboard-communication-log-detail.component';
import { AdminDashboardCommunicationsLogsTableComponent } from './admin-dashboard-communications-logs-table/admin-dashboard-communications-logs-table.component';
import { AdminDashboardCommunicationsLogsNestedTableComponent } from './admin-dashboard-communications-logs-nested-table/admin-dashboard-communications-logs-nested-table.component';

@NgModule({
  imports: [
    AccordionModule,
    ButtonModule,
    MessagesModule,
    MultiSelectModule,
    ProgressSpinnerModule,
    TableModule,

    CommonModule,
    FormsModule,
    RouterModule.forChild(adminDashboardCommunicationsLogsRoutes),
  ],
  declarations: [
    AdminDashboardCommunicationsLogsPageComponent,
    AdminDashboardCommunicationLogDetailComponent,
    AdminDashboardCommunicationsLogsTableComponent,
    AdminDashboardCommunicationsLogsNestedTableComponent,
  ],
})
export class AdminDashboardCommunicationsLogsModule {}

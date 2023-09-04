import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications-logs-page/communications-logs-page.component';
import { adminDashboardCommunicationsLogsRoutes } from './lib.routes';
import { RouterModule } from '@angular/router';
import { AccordionModule } from 'primeng/accordion';
import { MultiSelectModule } from 'primeng/multiselect';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { MessagesModule } from 'primeng/messages';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AdminDashboardCommunicationLogDetailComponent } from './communication-log-detail/communication-log-detail.component';
import { AdminDashboardCommunicationsLogsTableComponent } from './communications-logs-table/communications-logs-table.component';
import { AdminDashboardCommunicationsLogsNestedTableComponent } from './communications-logs-nested-table/communications-logs-nested-table.component';
import { SharedUtilAngularModule } from '@ewp-node-frontend/shared/util-angular';

@NgModule({
  imports: [
    AccordionModule,
    ButtonModule,
    CardModule,
    DropdownModule,
    MessagesModule,
    MultiSelectModule,
    ProgressSpinnerModule,
    TableModule,
    SharedUtilAngularModule,

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

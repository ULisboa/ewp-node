import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications-logs-page/communications-logs-page.component';
import { adminDashboardCommunicationsLogsRoutes } from './lib.routes';
import { RouterModule } from '@angular/router';
import { AccordionModule } from 'primeng/accordion';
import { MultiSelectModule } from 'primeng/multiselect';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { MessageModule } from 'primeng/message';
import { MessagesModule } from 'primeng/messages';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';
import { AdminDashboardCommunicationLogDetailComponent } from './communication-log-detail/communication-log-detail.component';
import { AdminDashboardCommunicationsLogsTableComponent } from './communications-logs-table/communications-logs-table.component';
import { AdminDashboardCommunicationsLogsNestedTableComponent } from './communications-logs-nested-table/communications-logs-nested-table.component';
import { SharedUtilAngularModule } from '@ewp-node-frontend/shared/util-angular';
import { AdminDashboardCommunicationLogReportToMonitoringFormComponent } from './communication-log-report-to-monitoring-form/communication-log-report-to-monitoring-form.component';
import { MessageService } from 'primeng/api';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { PrismComponent } from '@ewp-node-frontend/shared/ui-prism';

@NgModule({
  imports: [
    AccordionModule,
    ButtonModule,
    CardModule,
    DialogModule,
    DropdownModule,
    InputTextareaModule,
    MessageModule,
    MessagesModule,
    MultiSelectModule,
    ProgressSpinnerModule,
    TableModule,
    ToastModule,
    SharedUtilAngularModule,

    PrismComponent,

    ClipboardModule,

    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(adminDashboardCommunicationsLogsRoutes),
  ],
  declarations: [
    AdminDashboardCommunicationsLogsPageComponent,
    AdminDashboardCommunicationLogDetailComponent,
    AdminDashboardCommunicationsLogsTableComponent,
    AdminDashboardCommunicationsLogsNestedTableComponent,
    AdminDashboardCommunicationLogReportToMonitoringFormComponent,
  ],
  providers: [
    MessageService,
  ]
})
export class AdminDashboardCommunicationsLogsModule {}

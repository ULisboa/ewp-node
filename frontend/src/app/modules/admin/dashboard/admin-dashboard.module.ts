import { NgModule } from '@angular/core';

import { AdminDashboardRoutingModule } from './admin-dashboard-routing.module';
import { MessagesModule } from 'primeng/messages';
import { MessageService } from 'primeng/api';
import { AccordionModule } from 'primeng/accordion';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessageModule } from 'primeng/message';
import { MultiSelectModule } from 'primeng/multiselect';
import { PanelModule } from 'primeng/panel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { ToastModule } from 'primeng/toast';
import { AdminDashboardCommunicationsLogsSearchFormComponent } from './communications/logs/components/communications-logs-search-form/communications-logs-search-form.component';
import { AdminDashboardCommunicationLogChangeNotificationsDetailComponent } from './communications/logs/components/communication-log-change-notifications-detail/communication-log-change-notifications-detail.component';
import { AdminDashboardCommunicationLogChangeNotificationsTableComponent } from './communications/logs/components/communication-log-change-notifications-table/communication-log-change-notifications-table.component';
import { AdminDashboardCommunicationLogDetailComponent } from './communications/logs/components/communication-log-detail/communication-log-detail.component';
import { AdminDashboardCommunicationLogReportToMonitoringFormComponent } from './communications/logs/components/communication-log-report-to-monitoring-form/communication-log-report-to-monitoring-form.component';
import { AdminDashboardCommunicationsLogsTableComponent } from './communications/logs/components/communications-logs-table/communications-logs-table.component';
import { AdminDashboardCommunicationsLogsPageComponent } from './communications/logs/pages/communications-logs-page/communications-logs-page.component';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { PrismComponent } from '@ewp-node-frontend/shared/components';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [
    AdminDashboardCommunicationLogChangeNotificationsDetailComponent,
    AdminDashboardCommunicationLogChangeNotificationsTableComponent,
    AdminDashboardCommunicationLogDetailComponent,
    AdminDashboardCommunicationLogReportToMonitoringFormComponent,
    AdminDashboardCommunicationsLogsSearchFormComponent,
    AdminDashboardCommunicationsLogsTableComponent,

    AdminDashboardCommunicationsLogsPageComponent
  ],
  imports: [
    AccordionModule,
    ButtonModule,
    CalendarModule,
    CardModule,
    ConfirmDialogModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    MessageModule,
    MessagesModule,
    MultiSelectModule,
    PanelModule,
    ProgressSpinnerModule,
    TableModule,
    TabViewModule,
    ToastModule,

    ClipboardModule,

    PrismComponent,

    SharedModule,
    AdminDashboardRoutingModule
  ],
  providers: [
    MessageService
  ]
})
export class AdminDashboardModule { }

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
import { SharedUtilAngularModule } from '@ewp-node-frontend/shared/util-angular';
import { AdminDashboardCommunicationLogReportToMonitoringFormComponent } from './communication-log-report-to-monitoring-form/communication-log-report-to-monitoring-form.component';
import { MessageService } from 'primeng/api';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { PrismComponent } from '@ewp-node-frontend/shared/ui-prism';
import { CommunicationsLogsSearchFormComponent } from './communications-logs-search-form/communications-logs-search-form.component';
import { InputTextModule } from 'primeng/inputtext';
import {PanelModule} from 'primeng/panel';
import { AdminDashboardCommunicationLogChangeNotificationsTableComponent } from './communication-log-change-notifications-table/communication-log-change-notifications-table.component';
import { AdminDashboardCommunicationLogChangeNotificationsDetailComponent } from './communication-log-change-notifications-detail/communication-log-change-notifications-detail.component';
import { CalendarModule } from 'primeng/calendar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TabViewModule } from 'primeng/tabview';

@NgModule({
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
    SharedUtilAngularModule,

    PrismComponent,

    ClipboardModule,

    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(adminDashboardCommunicationsLogsRoutes),
  ],
  declarations: [
    AdminDashboardCommunicationLogChangeNotificationsDetailComponent,
    AdminDashboardCommunicationLogChangeNotificationsTableComponent,
    AdminDashboardCommunicationsLogsPageComponent,
    AdminDashboardCommunicationLogDetailComponent,
    AdminDashboardCommunicationsLogsTableComponent,
    AdminDashboardCommunicationLogReportToMonitoringFormComponent,
    CommunicationsLogsSearchFormComponent,
  ],
  providers: [MessageService],
})
export class AdminDashboardCommunicationsLogsModule {}

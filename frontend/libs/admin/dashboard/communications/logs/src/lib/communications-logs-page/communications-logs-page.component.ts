import { Component, inject } from '@angular/core';
import { CommunicationLogSummary } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { Message } from 'primeng/api';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { take } from 'rxjs';
import { TableLazyLoadEvent } from 'primeng/table';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';

@Component({
  selector: 'lib-admin-dashboard-communications-logs-page',
  templateUrl: './communications-logs-page.component.html',
  styleUrls: ['./communications-logs-page.component.scss'],
})
export class AdminDashboardCommunicationsLogsPageComponent {
  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);

  statusOptions = [
    { name: 'SUCCESS', value: 'SUCCESS' },
    { name: 'FAILURE', value: 'FAILURE' },
    { name: 'INCOMPLETE', value: 'INCOMPLETE' }
  ]

  selectedStatuses: string[] = [];

  communicationLogs!: CommunicationLogSummary[];
  totalResults = 0;

  loading = true;
  messages: Message[] = [];

  loadCommunicationsLogs(event: TableLazyLoadEvent) {
    this.loading = true;
    this.adminCommunicationsLogsService.getCommunicationsLogs({ format: 'primeng', filters: event.filters || {} }, event.first ?? 0, event.rows ?? 10)
      .pipe(take(1))
      .subscribe({
        next: response => {
          this.communicationLogs = response.data.communicationLogs;
          this.totalResults = response.data.totalResults;
          this.loading = false;
        },
        error: error => {
          this.messages = convertMessagesToPrimengFormat(error.messages as MessageInput[]);
          this.communicationLogs = [];
          this.totalResults = 0;
          this.loading = false;
        }
      })
  }
  
}

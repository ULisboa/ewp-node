import { Component, Input, inject } from '@angular/core';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { CommunicationLogSummary } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { TableLazyLoadEvent } from 'primeng/table';
import { take } from 'rxjs';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { Message } from 'primeng/api';

@Component({
  selector: 'lib-admin-dashboard-communications-logs-nested-table',
  templateUrl:
    './admin-dashboard-communications-logs-nested-table.component.html',
  styleUrls: [
    './admin-dashboard-communications-logs-nested-table.component.scss',
  ],
})
export class AdminDashboardCommunicationsLogsNestedTableComponent {

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);

  @Input() get communicationLogs(): CommunicationLogSummary[] {
    return this._communicationLogs;
  }

  set communicationLogs(val: CommunicationLogSummary[]) {
    this._communicationLogs = val;
  }

  statusOptions = [
    { name: 'SUCCESS', value: 'SUCCESS' },
    { name: 'FAILURE', value: 'FAILURE' },
    { name: 'INCOMPLETE', value: 'INCOMPLETE' }
  ]

  selectedStatuses: string[] = [];

  _communicationLogs!: CommunicationLogSummary[];
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

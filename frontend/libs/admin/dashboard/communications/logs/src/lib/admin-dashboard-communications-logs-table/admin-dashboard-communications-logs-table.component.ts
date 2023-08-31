import { Component, OnInit, inject } from '@angular/core';
import { CommunicationLogSummary, HttpCommunicationFromEwpNodeLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { FilterService, Message, SelectItem } from 'primeng/api';
import { TableLazyLoadEvent } from 'primeng/table';
import { take } from 'rxjs';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';

const CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME = 'communicationFromHeiId';
const CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME = 'communicationToHeiId';

@Component({
  selector: 'lib-admin-dashboard-communications-logs-table',
  templateUrl: './admin-dashboard-communications-logs-table.component.html',
  styleUrls: ['./admin-dashboard-communications-logs-table.component.scss'],
})
export class AdminDashboardCommunicationsLogsTableComponent implements OnInit {

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);
  filterService = inject(FilterService);

  sourceMatchModeOptions: SelectItem[];
  targetMatchModeOptions: SelectItem[];

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

  constructor() {
    this.sourceMatchModeOptions = [
      { label: 'Communication from HEI ID', value: CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME }
    ];

    this.targetMatchModeOptions = [
      { label: 'Communication to HEI ID', value: CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME }
    ];
  }

  ngOnInit() {
      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME, (value: object, filter: string): boolean => {
        if (filter === undefined || filter === null || filter.trim() === '') {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof HttpCommunicationFromEwpNodeLogDetail)) {
          return false;
        }

        return value.heiIdsCoveredByClient.includes(filter);
      });

      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME, (value: object, filter: string): boolean => {
        if (filter === undefined || filter === null || filter.trim() === '') {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof HttpCommunicationFromEwpNodeLogDetail)) {
          return false;
        }

        return value.heiIdsCoveredByClient.includes(filter);
      });
  }

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

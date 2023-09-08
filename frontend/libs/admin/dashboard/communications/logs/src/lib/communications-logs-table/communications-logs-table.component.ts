import { AfterContentInit, Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommunicationLogSummary, HttpCommunicationFromEwpNodeLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { FilterService, Message, MessageService, SelectItem } from 'primeng/api';
import { Table, TableLazyLoadEvent } from 'primeng/table';
import { take } from 'rxjs';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';

const CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME = 'communicationTypeIsOneOfSet';
const CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME = 'communicationFromHeiId';
const CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME = 'communicationToHeiId';

@Component({
  selector: 'lib-admin-dashboard-communications-logs-table',
  templateUrl: './communications-logs-table.component.html',
  styleUrls: ['./communications-logs-table.component.scss'],
})
export class AdminDashboardCommunicationsLogsTableComponent implements OnInit, AfterContentInit {

  @ViewChild('table', { static: true })
  table!: Table;

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);
  filterService = inject(FilterService);
  messageService = inject(MessageService);

  typeMatchModeOptions: SelectItem[];
  sourceMatchModeOptions: SelectItem[];
  targetMatchModeOptions: SelectItem[];

  typeOptions = [
    { name: 'EWP_IN', value: 'EWP_IN' },
    { name: 'EWP_OUT', value: 'EWP_OUT' },
    { name: 'HOST_IN', value: 'HOST_IN' },
    { name: 'HOST_OUT', value: 'HOST_OUT' },
    { name: 'HOST_PLUGIN_FUNCTION_CALL', value: 'HOST_PLUGIN_FUNCTION_CALL' }
  ]

  statusOptions = [
    { name: 'SUCCESS', value: 'SUCCESS' },
    { name: 'FAILURE', value: 'FAILURE' },
    { name: 'INCOMPLETE', value: 'INCOMPLETE' }
  ]

  selectedTypes: string[] = [];
  selectedStatuses: string[] = [];

  communicationLogs!: CommunicationLogSummary[];
  totalResults = 0;

  loading = true;
  messages: Message[] = [];
  lastTableLazyLoadEvent?: TableLazyLoadEvent;

  constructor() {
    this.typeMatchModeOptions = [
      { label: 'Communication Type', value: CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME }
    ];

    this.sourceMatchModeOptions = [
      { label: 'Communication from HEI ID', value: CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME }
    ];

    this.targetMatchModeOptions = [
      { label: 'Communication to HEI ID', value: CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME }
    ];
  }

  ngOnInit() {
      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME, (value: object, filter: string): boolean => {
        if (filter === undefined || filter === null || filter.trim() === '') {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof CommunicationLogSummary)) {
          return false;
        }

        return value.type === filter;
      });

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

  ngAfterContentInit() {
    this.selectedTypes = ['EWP_IN', 'EWP_OUT', 'HOST_IN'];
    this.table.filters['type'] = [{
      value: this.selectedTypes, 
      matchMode: CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME, 
      operator: 'and' 
    }];
  }

  loadCommunicationsLogs(event: TableLazyLoadEvent) {
    this.lastTableLazyLoadEvent = event;
    this.loading = true;
    this.messages = [];
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

  onCommunicationReportedToMonitoring() {
    this.messageService.add({ key: 'tc', severity: 'success', summary: 'Communication was reported to monitoring successfully'})
    if (this.lastTableLazyLoadEvent) {
      this.loadCommunicationsLogs(this.lastTableLazyLoadEvent);
    }
  }

}

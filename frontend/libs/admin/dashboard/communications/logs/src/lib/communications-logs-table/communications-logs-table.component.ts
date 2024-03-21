import { AfterContentInit, AfterViewInit, Component, Input, OnInit, ViewChild, inject } from '@angular/core';
import { CommunicationLogSummary, HttpCommunicationFromEwpNodeLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { FilterService, Message, MessageService, SelectItem } from 'primeng/api';
import { Table, TableLazyLoadEvent } from 'primeng/table';
import { take } from 'rxjs';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { convertFilters } from '@ewp-node-frontend/shared/util-primeng';

const CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME = 'communicationTypeIsOneOfSet';
const CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME = 'communicationFromHeiId';
const CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME = 'communicationToHeiId';

@Component({
  selector: 'lib-admin-dashboard-communications-logs-table',
  templateUrl: './communications-logs-table.component.html',
  styleUrls: ['./communications-logs-table.component.scss'],
})
export class AdminDashboardCommunicationsLogsTableComponent implements AfterContentInit {

  @ViewChild('table', { static: true })
  table!: Table;

  @Input()
  lazyLoad = true;

  @Input()
  allowFiltering = true;

  _communicationLogs!: CommunicationLogSummary[];

  @Input() get communicationLogs(): CommunicationLogSummary[] {
    return this._communicationLogs;
  }

  set communicationLogs(val: CommunicationLogSummary[]) {
    this._communicationLogs = val;
    this.totalResults = this._communicationLogs ? this._communicationLogs.length : 0;
    this.loading = false;
  }

  private _additionalFilter: object | undefined;

  @Input()
  set additionalFilter(value: object | undefined) {
    this._additionalFilter = value;
    if (this.lastTableLazyLoadEvent) {
      this.loadCommunicationsLogs(this.lastTableLazyLoadEvent);
    }
  }

  get additionalFilter(): object | undefined {
    return this._additionalFilter;
  }

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

  ngAfterContentInit() {
    if (this.allowFiltering) {
      this.selectedTypes = ['EWP_IN', 'HOST_IN'];
      this.table.filters['type'] = [{
        value: this.selectedTypes, 
        matchMode: CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME, 
        operator: 'and' 
      }];

      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME, (value: object, filter: string[]): boolean => {
        if (filter === undefined || filter === null || filter.length === 0) {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof CommunicationLogSummary)) {
          return false;
        }

        return filter.includes(value.type);
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
  }

  loadCommunicationsLogs(event: TableLazyLoadEvent) {
    this.lastTableLazyLoadEvent = event;
    this.loading = true;
    this.messages = [];
    const subFilters = [];
    if (this.allowFiltering) {
      if (event.filters) {
        const convertedFilters = convertFilters(event.filters);
        subFilters.push(convertedFilters);
      }
      if (this.additionalFilter) {
        subFilters.push(this.additionalFilter);
      }
    }
    const filter = {
      type: 'CONJUNCTION',
      subFilters: subFilters
    };
    this.adminCommunicationsLogsService.getCommunicationsLogs(filter, event.first ?? 0, event.rows ?? 10)
      .pipe(take(1))
      .subscribe({
        next: response => {
          this._communicationLogs = response.data.communicationLogs;
          this.totalResults = response.data.totalResults;
          this.loading = false;
        },
        error: error => {
          this.messages = convertMessagesToPrimengFormat(error.messages as MessageInput[]);
          this._communicationLogs = [];
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

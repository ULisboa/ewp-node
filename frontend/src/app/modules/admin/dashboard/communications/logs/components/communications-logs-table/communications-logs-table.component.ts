import { AfterContentInit, Component, Input, ViewChild, inject } from '@angular/core';
import { FilterService, ToastMessageOptions, MessageService, SelectItem } from 'primeng/api';
import { Table, TableLazyLoadEvent } from 'primeng/table';
import { take } from 'rxjs';
import { CommunicationLogSummary, HttpCommunicationFromEwpNodeLogDetail } from '../../../../../../../shared/models';
import { AdminCommunicationsLogsService } from '../../../../../../../core/services/admin/communications/logs/admin-communications-logs.service';
import { convertFilters } from '../../../../../../../shared/utils/primeng';
import { convertMessagesToPrimengFormat, MessageInput } from '../../../../../../../shared/utils/message';

const CUSTOM_FILTER_COMMUNICATION_TYPE_IS_ONE_OF_SET_NAME = 'COMMUNICATION-LOG-TYPE-IS-ONE-OF-SET';
const CUSTOM_FILTER_COMMUNICATION_FROM_HEI_ID_NAME = 'HTTP-COMMUNICATION-FROM-EWP-NODE-IS-FROM-HEI-ID';
const CUSTOM_FILTER_COMMUNICATION_TO_HEI_ID_NAME = 'HTTP-COMMUNICATION-TO-EWP-NODE-IS-TO-HEI-ID';
const CUSTOM_FILTER_COMMUNICATION_LOG_START_PROCESSING_AFTER_OR_EQUAL_DATE_TIME_NAME = 'COMMUNICATION-LOG-START-PROCESSING-AFTER-OR-EQUAL-DATE-TIME';
const CUSTOM_FILTER_COMMUNICATION_LOG_END_PROCESSING_BEFORE_OR_EQUAL_DATE_TIME_NAME = 'COMMUNICATION-LOG-END-PROCESSING-BEFORE-OR-EQUAL-DATE-TIME';

@Component({
    selector: 'app-admin-dashboard-communications-logs-table',
    templateUrl: './communications-logs-table.component.html',
    styleUrls: ['./communications-logs-table.component.scss'],
    standalone: false
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

  freeTextSearchValue: string | undefined;

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
  startProcessingDateTimeMatchModeOptions: SelectItem[];
  endProcessingDateTimeMatchModeOptions: SelectItem[];

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
  selectedAfterOrEqualStartProcessingDateTime: Date | null = null;
  selectedBeforeOrEqualEndProcessingDateTime: Date | null = null;

  totalResults = 0;

  loading = true;
  messages: ToastMessageOptions[] = [];
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

    this.startProcessingDateTimeMatchModeOptions = [
      { label: 'After or equal than', value: CUSTOM_FILTER_COMMUNICATION_LOG_START_PROCESSING_AFTER_OR_EQUAL_DATE_TIME_NAME }
    ];

    this.endProcessingDateTimeMatchModeOptions = [
      { label: 'Before or equal than', value: CUSTOM_FILTER_COMMUNICATION_LOG_END_PROCESSING_BEFORE_OR_EQUAL_DATE_TIME_NAME }
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

      this.selectedAfterOrEqualStartProcessingDateTime = new Date();
      this.selectedAfterOrEqualStartProcessingDateTime.setDate(this.selectedAfterOrEqualStartProcessingDateTime.getDate() - 3);
      this.table.filters['startProcessingDateTime'] = [{
        value: this.selectedAfterOrEqualStartProcessingDateTime, 
        matchMode: CUSTOM_FILTER_COMMUNICATION_LOG_START_PROCESSING_AFTER_OR_EQUAL_DATE_TIME_NAME, 
        operator: 'and' 
      }];

      this.selectedBeforeOrEqualEndProcessingDateTime = new Date();
      this.table.filters['endProcessingDateTime'] = [{
        value: this.selectedBeforeOrEqualEndProcessingDateTime, 
        matchMode: CUSTOM_FILTER_COMMUNICATION_LOG_END_PROCESSING_BEFORE_OR_EQUAL_DATE_TIME_NAME, 
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

      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_LOG_START_PROCESSING_AFTER_OR_EQUAL_DATE_TIME_NAME, (value: object, filter: number): boolean => {
        if (filter === undefined || filter === null) {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof CommunicationLogSummary)) {
          return false;
        }

        if (!value.startProcessingDateTime) {
          return false;
        }

        return value.startProcessingDateTime.getTime()>= filter;
      });

      this.filterService.register(CUSTOM_FILTER_COMMUNICATION_LOG_END_PROCESSING_BEFORE_OR_EQUAL_DATE_TIME_NAME, (value: object, filter: number): boolean => {
        if (filter === undefined || filter === null) {
          return true;
        }

        if (value === undefined || value === null) {
          return false;
        }

        if (!(value instanceof CommunicationLogSummary)) {
          return false;
        }

        if (!value.endProcessingDateTime) {
          return false;
        }

        return value.endProcessingDateTime.getTime() <= filter;
      });
    }
  }

  onFreeTextSearch() {
    this.loadCommunicationsLogs(this.lastTableLazyLoadEvent || { filters: {} });
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
      if (this.freeTextSearchValue && this.freeTextSearchValue.length > 0) {
        subFilters.push({
          type: 'COMMUNICATION-LOG-FREE-TEXT',
          value: this.freeTextSearchValue
        });
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

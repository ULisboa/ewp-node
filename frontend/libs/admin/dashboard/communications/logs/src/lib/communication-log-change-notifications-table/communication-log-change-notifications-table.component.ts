import { Component, Input, OnInit, inject } from '@angular/core';
import { EwpChangeNotification } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { Message, MessageService } from 'primeng/api';
import { AdminEwpChangeNotificationsService } from '../services/admin-ewp-change-notifications.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { TableLazyLoadEvent } from 'primeng/table';
import { convertFilters } from '@ewp-node-frontend/shared/util-primeng';
import { take } from 'rxjs';

@Component({
  selector: 'lib-admin-dashboard-communication-log-change-notifications-table',
  templateUrl: './communication-log-change-notifications-table.component.html',
  styleUrls: ['./communication-log-change-notifications-table.component.scss']
})
export class AdminDashboardCommunicationLogChangeNotificationsTableComponent {

  @Input()
  ids: number[] = [];

  private _additionalFilter: object | undefined;

  @Input()
  set additionalFilter(value: object | undefined) {
    this._additionalFilter = value;
    if (this.lastTableLazyLoadEvent) {
      this.loadEwpChangeNotifications();
    }
  }

  get additionalFilter(): object | undefined {
    return this._additionalFilter;
  }

  _ewpChangeNotifications!: EwpChangeNotification[];

  @Input() get ewpChangeNotifications(): EwpChangeNotification[] {
    return this._ewpChangeNotifications;
  }

  set ewpChangeNotifications(val: EwpChangeNotification[]) {
    this._ewpChangeNotifications = val;
    this.totalResults = this._ewpChangeNotifications ? this._ewpChangeNotifications.length : 0;
    this.loading = false;
  }

  totalResults: number = 0;

  loading = true;
  messages: Message[] = [];
  lastTableLazyLoadEvent?: TableLazyLoadEvent;

  adminEwpChangeNotificationsService = inject(AdminEwpChangeNotificationsService);

  constructor(private messageService: MessageService) {}

  loadEwpChangeNotifications() {
    this.onLoadEwpChangeNotifications(this.lastTableLazyLoadEvent || {});
  }

  onLoadEwpChangeNotifications(event: TableLazyLoadEvent) {
    this.lastTableLazyLoadEvent = event;
    this.loading = true;
    this.messages = [];
    const subFilters = [];
    if (event.filters) {
      const convertedFilters = convertFilters(event.filters);
      subFilters.push(convertedFilters);
    }
    if (this.ids) {
      subFilters.push({
        type: 'IN',
        field: 'id',
        values: this.ids
      })
    }
    if (this.additionalFilter) {
      subFilters.push(this.additionalFilter);
    }
    const filter = {
      type: 'CONJUNCTION',
      subFilters: subFilters
    };
    this.adminEwpChangeNotificationsService.getEwpChangeNotifications(filter, event.first ?? 0, event.rows ?? 10)
      .pipe(take(1))
      .subscribe({
        next: response => {
          this._ewpChangeNotifications = response.ewpChangeNotifications;
          this.totalResults = response.totalResults;
          this.loading = false;
        },
        error: error => {
          this.messages = convertMessagesToPrimengFormat(error.messages as MessageInput[]);
          this._ewpChangeNotifications = [];
          this.totalResults = 0;
          this.loading = false;
        }
      })
  }

}

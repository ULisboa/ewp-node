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

  additionalFilter?: object;

  onFilter(filter: object) {
    this.additionalFilter = filter;
  }
  
}

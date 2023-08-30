import { Component, Input, OnInit, inject } from '@angular/core';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { CommunicationLogDetail, HostPluginFunctionCallCommunicationLogDetail, HttpCommunicationLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { Message } from 'primeng/api';

@Component({
  selector: 'lib-admin-dashboard-communication-log-detail',
  templateUrl: './admin-dashboard-communication-log-detail.component.html',
  styleUrls: ['./admin-dashboard-communication-log-detail.component.scss'],
})
export class AdminDashboardCommunicationLogDetailComponent implements OnInit {
  public readonly HostPluginFunctionCallCommunicationLogDetail = HostPluginFunctionCallCommunicationLogDetail;
  public readonly HttpCommunicationLogDetail = HttpCommunicationLogDetail;

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);

  @Input()
  id!: number;

  communicationLog?: CommunicationLogDetail | HostPluginFunctionCallCommunicationLogDetail;

  loading = true;
  messages: Message[] = [];

  ngOnInit() {
    this.loading = true;
    this.adminCommunicationsLogsService.getCommunicationsLogInDetail(this.id).subscribe({
      next: response => {
        this.messages = [];
        this.communicationLog = response.data;
        this.loading = false;
      },
      error: error => {
        this.messages = convertMessagesToPrimengFormat(error.messages as MessageInput[]);
        this.communicationLog = undefined;
        this.loading = false;
      }
    })
  }

}

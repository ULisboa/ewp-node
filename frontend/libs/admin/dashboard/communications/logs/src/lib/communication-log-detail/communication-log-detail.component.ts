import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject } from '@angular/core';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { CommunicationLogDetail, EwpHttpCommunicationLogDetail, FunctionCallCommunicationLogDetail, HostPluginFunctionCallCommunicationLogDetail, HttpCommunicationFromEwpNodeLogDetail, HttpCommunicationLogDetail, HttpCommunicationToEwpNodeLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { Message, MessageService } from 'primeng/api';

@Component({
  selector: 'lib-admin-dashboard-communication-log-detail',
  templateUrl: './communication-log-detail.component.html',
  styleUrls: ['./communication-log-detail.component.scss']
})
export class AdminDashboardCommunicationLogDetailComponent implements OnInit {
  public readonly EwpHttpCommunicationLogDetail = EwpHttpCommunicationLogDetail;
  public readonly HttpCommunicationFromEwpNodeLogDetail = HttpCommunicationFromEwpNodeLogDetail;
  public readonly HttpCommunicationToEwpNodeLogDetail = HttpCommunicationToEwpNodeLogDetail;
  public readonly FunctionCallCommunicationLogDetail = FunctionCallCommunicationLogDetail;
  public readonly HostPluginFunctionCallCommunicationLogDetail = HostPluginFunctionCallCommunicationLogDetail;
  public readonly HttpCommunicationLogDetail = HttpCommunicationLogDetail;

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);

  @Input()
  id!: number;

  @Output()
  communicationReportedToMonitoring = new EventEmitter<any>();

  @ViewChild('codeElem')
  codeElem!: ElementRef;

  communicationLog?: CommunicationLogDetail | HostPluginFunctionCallCommunicationLogDetail;

  loading = true;
  messages: Message[] = [];

  constructor(private messageService: MessageService) {}

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

  onCommunicationReportedToMonitoring() {
    this.communicationReportedToMonitoring.emit();
  }

  onCopyToClipboard(successful: boolean) {
    if (successful) {
      this.messageService.add({ severity: 'success', summary: 'Success' });
    } else {
      this.messageService.add({ severity: 'error', summary: 'Error' });
    }
  }

}

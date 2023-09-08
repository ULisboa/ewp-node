import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpCommunicationToEwpNodeLogDetail } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { AdminCommunicationsLogsService } from '../services/admin-communications-logs.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';
import { take } from 'rxjs';
import { Message } from 'primeng/api';

@Component({
  selector: 'lib-admin-dashboard-communication-log-report-to-monitoring-form',
  templateUrl: './communication-log-report-to-monitoring-form.component.html',
  styleUrls: ['./communication-log-report-to-monitoring-form.component.scss'],
})
export class AdminDashboardCommunicationLogReportToMonitoringFormComponent {

  adminCommunicationsLogsService = inject(AdminCommunicationsLogsService);

  @Input()
  communicationLog!: HttpCommunicationToEwpNodeLogDetail;

  @Output()
  communicationReportedToMonitoring = new EventEmitter<any>();

  form = new FormGroup({
    clientMessage: new FormControl('', [Validators.required])
  });

  pendingSubmit = false;
  messages: Message[] = [];

  reportToMonitoring() {
    if (this.form.valid) {
      this.pendingSubmit = true;
      this.messages = [];
      this.adminCommunicationsLogsService.reportCommunicationToMonitoring(this.communicationLog.id, this.form.value.clientMessage)
      .pipe(take(1))
      .subscribe({
        next: response => {
          this.pendingSubmit = false;
          this.communicationReportedToMonitoring.emit();
        },
        error: error => {
          this.messages = convertMessagesToPrimengFormat(error.messages as MessageInput[]);
          this.pendingSubmit = false;
        }
      })
    }
  }
}

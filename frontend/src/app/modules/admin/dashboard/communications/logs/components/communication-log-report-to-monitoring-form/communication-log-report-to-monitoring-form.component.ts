import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { take } from 'rxjs';
import { ToastMessageOptions } from 'primeng/api';
import { HttpCommunicationToEwpNodeLogDetail } from '@ewp-node-frontend/shared/models';
import { convertMessagesToPrimengFormat, MessageInput } from '@ewp-node-frontend/shared/utils/message';
import { AdminCommunicationsLogsService } from '@ewp-node-frontend/core';

@Component({
    selector: 'app-admin-dashboard-communication-log-report-to-monitoring-form',
    templateUrl: './communication-log-report-to-monitoring-form.component.html',
    standalone: false
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
  messages: ToastMessageOptions[] = [];

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

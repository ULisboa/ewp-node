import { Component, Input } from '@angular/core';
import { EwpChangeNotification } from '@ewp-node-frontend/shared/models';
import { ToastMessageOptions } from 'primeng/api';

@Component({
    selector: 'app-admin-dashboard-communication-log-change-notifications-detail',
    templateUrl: './communication-log-change-notifications-detail.component.html',
    standalone: false,
})
export class AdminDashboardCommunicationLogChangeNotificationsDetailComponent {

  @Input()
  ewpChangeNotification!: EwpChangeNotification;
  
  messages: ToastMessageOptions[] = [];

}

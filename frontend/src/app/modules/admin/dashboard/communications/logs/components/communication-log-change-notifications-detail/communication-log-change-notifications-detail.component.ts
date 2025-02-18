import { Component, Input } from '@angular/core';
import { EwpChangeNotification } from '@ewp-node-frontend/shared/models';
import { Message } from 'primeng/api';

@Component({
  selector: 'app-admin-dashboard-communication-log-change-notifications-detail',
  templateUrl: './communication-log-change-notifications-detail.component.html'
})
export class AdminDashboardCommunicationLogChangeNotificationsDetailComponent {

  @Input()
  ewpChangeNotification!: EwpChangeNotification;
  
  messages: Message[] = [];

}

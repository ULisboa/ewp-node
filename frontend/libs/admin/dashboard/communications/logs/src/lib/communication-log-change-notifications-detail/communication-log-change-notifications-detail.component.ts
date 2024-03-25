import { Component, Input, OnInit, inject } from '@angular/core';
import { EwpChangeNotification } from '@ewp-node-frontend/admin/shared/api-interfaces';
import { Message, MessageService } from 'primeng/api';
import { AdminEwpChangeNotificationsService } from '../services/admin-ewp-change-notifications.service';
import { MessageInput, convertMessagesToPrimengFormat } from '@ewp-node-frontend/admin/shared/util-primeng';

@Component({
  selector: 'lib-admin-dashboard-communication-log-change-notifications-detail',
  templateUrl: './communication-log-change-notifications-detail.component.html',
  styleUrls: ['./communication-log-change-notifications-detail.component.scss']
})
export class AdminDashboardCommunicationLogChangeNotificationsDetailComponent {

  @Input()
  ewpChangeNotification!: EwpChangeNotification;
  
  messages: Message[] = [];

  constructor(private messageService: MessageService) {}

}

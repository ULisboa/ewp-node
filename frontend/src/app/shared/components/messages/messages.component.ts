
import { Component, Input } from '@angular/core';
import { convertSeverityToPrimengFormat } from '@ewp-node-frontend/shared/utils/message';
import { ToastMessageOptions } from 'primeng/api';
import { MessageModule } from 'primeng/message';

@Component({
    imports: [MessageModule],
    selector: 'app-messages',
    templateUrl: './messages.component.html',
    standalone: true
})
export class MessagesComponent {

  @Input()
  messages: ToastMessageOptions[] = [];

  @Input()
  closable = true;
  
  // NOTE: necessary to allow .html file to use this function
  convertSeverityToPrimengFormat = convertSeverityToPrimengFormat;
}

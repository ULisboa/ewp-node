import { Component } from '@angular/core';

@Component({
    selector: 'app-admin-dashboard-communications-logs-page',
    templateUrl: './communications-logs-page.component.html',
    standalone: false
})
export class AdminDashboardCommunicationsLogsPageComponent {

  additionalFilter?: object;

  onFilter(filter: object) {
    this.additionalFilter = filter;
  }
  
}

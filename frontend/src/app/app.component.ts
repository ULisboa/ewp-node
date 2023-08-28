import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NxWelcomeComponent } from './nx-welcome.component';
import { AdminAuthDataAccessModule } from '@ewp-node-frontend/admin/auth/data-access';

@Component({
  standalone: true,
  imports: [NxWelcomeComponent, AdminAuthDataAccessModule, RouterModule],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'ewp-node-frontend';
}

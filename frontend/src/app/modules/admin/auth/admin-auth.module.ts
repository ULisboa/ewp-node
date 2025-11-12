import { NgModule } from '@angular/core';

import { AdminAuthRoutingModule } from './admin-auth-routing.module';
import { AdminAuthLoginPageComponent } from './pages/admin-auth-login-page/admin-auth-login-page.component';
import { SharedModule } from '../../../../../src/app/shared/shared.module';
import { MessageModule } from 'primeng/message';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessagesComponent } from 'src/app/shared/components/messages/messages.component';


@NgModule({
  declarations: [
    AdminAuthLoginPageComponent
  ],
  imports: [
    ButtonModule,
    PasswordModule,
    MessageModule,
    MessagesComponent,
    SharedModule,
    AdminAuthRoutingModule
  ],
  providers: [
  ]
})
export class AdminAuthModule { }

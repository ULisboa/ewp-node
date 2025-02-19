import { NgModule } from '@angular/core';

import { AdminAuthRoutingModule } from './admin-auth-routing.module';
import { AdminAuthLoginPageComponent } from './pages/admin-auth-login-page/admin-auth-login-page.component';
import { SharedModule } from '../../../../../src/app/shared/shared.module';
import { MessagesModule } from 'primeng/messages';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';


@NgModule({
  declarations: [
    AdminAuthLoginPageComponent
  ],
  imports: [
    ButtonModule,
    MessagesModule,
    PasswordModule,

    SharedModule,
    AdminAuthRoutingModule
  ]
})
export class AdminAuthModule { }

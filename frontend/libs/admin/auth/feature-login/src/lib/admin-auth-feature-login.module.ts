import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminLoginFormComponent } from './admin-login-form/admin-login-form.component';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { InputTextModule } from 'primeng/inputtext';
import { MessagesModule } from 'primeng/messages';
import { RouterModule, Routes } from '@angular/router';
import { AdminAuthDataAccessModule } from '@ewp-node-frontend/admin/auth/data-access';

const routes: Routes = [
  {
    path: '**',
    component: AdminLoginFormComponent
  }
];

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    CheckboxModule,
    InputTextModule,
    FormsModule,
    PasswordModule,
    MessagesModule,
    AdminAuthDataAccessModule,
    RouterModule.forChild(routes)
  ],
  declarations: [AdminLoginFormComponent],
  exports: [],
})
export class AdminAuthFeatureLoginModule { }

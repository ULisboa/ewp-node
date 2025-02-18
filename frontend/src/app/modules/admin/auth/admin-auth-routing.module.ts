import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminAuthLoginPageComponent } from './pages/admin-auth-login-page/admin-auth-login-page.component';

const routes: Routes = [
  {
    path: '**',
    component: AdminAuthLoginPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminAuthRoutingModule { }

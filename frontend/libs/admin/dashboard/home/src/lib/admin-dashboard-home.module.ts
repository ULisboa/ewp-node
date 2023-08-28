import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { adminDashboardHomeRoutes } from './lib.routes';
import { AdminDashboardHomeComponent } from './admin-dashboard-home/admin-dashboard-home.component';

@NgModule({
  imports: [CommonModule, RouterModule.forChild(adminDashboardHomeRoutes)],
  declarations: [AdminDashboardHomeComponent],
})
export class AdminDashboardHomeModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideHttpClient, withRequestsMadeViaParent } from '@angular/common/http';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideHttpClient(withRequestsMadeViaParent())
  ]
})
export class AdminAuthDataAccessModule {}

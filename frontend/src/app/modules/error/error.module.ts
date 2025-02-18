import { NgModule } from '@angular/core';

import { ErrorRoutingModule } from './error-routing.module';
import { NotFoundPageComponent } from './pages/not-found-page/not-found-page.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [NotFoundPageComponent],
  imports: [
    SharedModule,
    ErrorRoutingModule
  ]
})
export class ErrorModule { }

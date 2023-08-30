import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InstanceOfPipe } from './pipes/instance-of.pipe';

@NgModule({
  imports: [CommonModule],
  exports: [InstanceOfPipe],
  declarations: [InstanceOfPipe],
})
export class SharedUtilAngularModule {}

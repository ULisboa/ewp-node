import { NgModule } from "@angular/core";
import { InstanceOfPipe } from "./pipes/instance-of/instance-of.pipe";
import { CommonModule } from "@angular/common";
import { PrismComponent } from "./components/prism/prism.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@NgModule({
  declarations: [
    InstanceOfPipe
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    
    PrismComponent
  ],
  exports: [
    InstanceOfPipe,

    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class SharedModule { }

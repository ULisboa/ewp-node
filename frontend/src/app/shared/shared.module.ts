import { NgModule } from "@angular/core";
import { InstanceOfPipe } from "./pipes/instance-of/instance-of.pipe";
import { CommonModule, NgClass } from "@angular/common";
import { PrismComponent } from "./components/prism/prism.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@NgModule({
  declarations: [
    InstanceOfPipe
  ],
  providers: [
    CommonModule
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    
    PrismComponent
  ],
  exports: [
    CommonModule,

    InstanceOfPipe,

    FormsModule,
    ReactiveFormsModule
  ]
})
export class SharedModule { }

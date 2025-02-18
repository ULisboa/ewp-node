import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideHttpClient, withRequestsMadeViaParent } from '@angular/common/http';

@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ]
})
export class CoreModule {
  static forRoot(): ModuleWithProviders<CoreModule> {
    return {
      ngModule: CoreModule,
      providers: [
        provideHttpClient(withRequestsMadeViaParent())
      ]
    };
  }
}

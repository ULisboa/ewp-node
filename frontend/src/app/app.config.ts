import { APP_INITIALIZER, ApplicationConfig } from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
} from '@angular/router';
import { appRoutes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { withXsrfConfiguration } from '@angular/common/http';
import { xsrfInterceptor } from '@ewp-node-frontend/shared/data-access-shared';
import { Store, provideStore } from '@ngrx/store';
import { adminAuthActions, adminAuthFeature, adminAuthFunctionalEffects } from '@ewp-node-frontend/admin/auth/data-access';
import { provideEffects } from '@ngrx/effects';
import { provideRouterStore } from '@ngrx/router-store';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes, withEnabledBlockingInitialNavigation()),
    provideHttpClient(withInterceptors([xsrfInterceptor]), withXsrfConfiguration({ cookieName: 'XSRF-TOKEN' })),
    provideAnimations(),
    provideStore({
      adminAuth: adminAuthFeature.reducer
    }),
    provideEffects(adminAuthFunctionalEffects),
    provideRouterStore(),
    !environment.production ? provideStoreDevtools({connectInZone: true}) : [],
    {
      provide: APP_INITIALIZER,
      useFactory: (store: Store) => {
        return () => {
          store.dispatch(adminAuthActions.init());
        };
      },
      multi: true,
      deps: [Store]
    }
  ],
};

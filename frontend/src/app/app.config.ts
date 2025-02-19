import { ApplicationConfig, inject, provideAppInitializer } from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
} from '@angular/router';
import { appRoutes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { withXsrfConfiguration } from '@angular/common/http';
import { Store, provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideRouterStore } from '@ngrx/router-store';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { adminAuthActions, adminAuthFeature, adminAuthFunctionalEffects } from './shared/states';
import { xsrfInterceptor } from './core/interceptors/xsrf.interceptor';
import { providePrimeNG } from 'primeng/config';
import Lara from '@primeng/themes/lara';
import { definePreset } from '@primeng/themes';

const PrimengPreset = definePreset(Lara, {
  semantic: {
      primary: {
          50: '{indigo.50}',
          100: '{indigo.100}',
          200: '{indigo.200}',
          300: '{indigo.300}',
          400: '{indigo.400}',
          500: '{indigo.500}',
          600: '{indigo.600}',
          700: '{indigo.700}',
          800: '{indigo.800}',
          900: '{indigo.900}',
          950: '{indigo.950}'
      }
  }
});

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
    providePrimeNG({
      theme: { 
        preset: PrimengPreset, 
        options: { 
          darkModeSelector: '.app-dark' 
        } 
      }
    }),
    provideAppInitializer(() => {
        const initializerFn = ((store: Store) => {
        return () => {
          store.dispatch(adminAuthActions.init());
        };
      })(inject(Store));
        return initializerFn();
      })
  ],
};

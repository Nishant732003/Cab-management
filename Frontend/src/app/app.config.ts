

import { ApplicationConfig, isDevMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS, withFetch } from '@angular/common/http';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideStore } from '@ngrx/store';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { routes } from './app.routes';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import userAuthReducer from './redux/slice/userAuthSlice';
import driverAuthReducer from './redux/slice/driverAuthslice';
import adminAuthReducer from './redux/slice/adminAuthslice';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration(),
    // provideAnimations(),
    provideHttpClient(withFetch()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },

    provideStore({
      user: userAuthReducer as any,
      driver: driverAuthReducer as any,
      admin: adminAuthReducer as any,
    }),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: !isDevMode(),
    }),
  ]
};
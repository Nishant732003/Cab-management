import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http'; // FIX: Import withInterceptorsFromDi
import { HTTP_INTERCEPTORS } from '@angular/common/http'; // FIX: Import HTTP_INTERCEPTORS

import { routes } from './app.routes';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor'; // FIX: Import our interceptor

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // FIX: Configure the HTTP client and register our interceptor
    provideHttpClient(withInterceptorsFromDi()), 
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ]
};
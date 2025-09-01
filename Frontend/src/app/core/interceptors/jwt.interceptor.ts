import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get the token from local storage
    const token = localStorage.getItem('jwt_token');
    
    // Check if the request is going to our API URL
    const isApiUrl = request.url.startsWith(environment.apiUrl);

    // If a token exists and it's an API request, clone the request to add the new header.
    if (token && isApiUrl) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // Pass the cloned or original request to the next handler
    return next.handle(request);
  }
}

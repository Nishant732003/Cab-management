// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    
    // Check if user is logged in
    if (this.authService.isLoggedIn()) {
      return true; // Allow access to the route
    }

    // User is not logged in - redirect to login page
    // You can also pass the return URL so you can redirect back after login
    this.router.navigate(['/login'], { 
      queryParams: { returnUrl: state.url } 
    });
    
    return false; // Block access to the route
  }
}
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app.component.html',
})
export class AppComponent {
  currentYear = new Date().getFullYear();
  isAuthenticated = false;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.checkAuthStatus();
    
    this.router.events.subscribe(() => {
      this.checkAuthStatus();
    });
  }
  // A single check is sufficient, ngOnInit is a good place for it--- added by me
  ngOnInit(): void {
    this.checkAuthStatus();
  }


  checkAuthStatus(): void {
    if (isPlatformBrowser(this.platformId)) {
      const user = localStorage.getItem('currentUser');
      this.isAuthenticated = !!user;

      // Define all the routes that an unauthenticated user is allowed to see---- added by me
      const publicRoutes = ['/login', '/role', '/registeruser', '/registerdriver'];
      const currentPath = window.location.pathname;

      
      // If the user is not authenticated AND they are not on a public route, redirect them.---- added by me
      if (!this.isAuthenticated && !publicRoutes.includes(currentPath)) {
        this.router.navigate(['/role']); // Redirect to role selection, not login
      }
    }
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      this.isAuthenticated = false;
      this.router.navigate(['/role']); // Go to role selection after logout ---- added by me
    }
  }
}
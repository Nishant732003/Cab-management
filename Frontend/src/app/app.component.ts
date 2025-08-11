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

  checkAuthStatus(): void {
    if (isPlatformBrowser(this.platformId)) {
      const user = localStorage.getItem('currentUser');
      this.isAuthenticated = !!user;
      
      if (!this.isAuthenticated && !window.location.href.includes('/login')) {
        this.router.navigate(['/login']);
      }
    }
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
      this.isAuthenticated = false;
      this.router.navigate(['/login']);
    }
  }
}
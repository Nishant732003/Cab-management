import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'driver-navbar',
  standalone: false, 
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  // User information
  user: any = null;
  isAuthenticated = false;
  
  constructor(
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getUserFromLocalStorage();
  }

  /**
   * Get user information from localStorage
   */
  private getUserFromLocalStorage(): void {
    try {
      const userStr = localStorage.getItem('currentUser');
      
      if (userStr) {
        this.user = JSON.parse(userStr);
        this.isAuthenticated = true;
      } else {
        console.warn('No user found in localStorage');
        this.isAuthenticated = false;
      }
    } catch (error) {
      console.error('Error parsing user data from localStorage:', error);
      this.isAuthenticated = false;
    }
  }

  /**
   * Handle user logout
   */
  handleLogout(event: Event): void {

    
    // Clear user data from localStorage
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authToken');
    
    // Redirect to login page
     this.router.navigate(['/login'], { replaceUrl: true });
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(userName?: string): string {
    if (!userName) return 'D';
    
    return userName
      .split(' ')
      .map(name => name.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }
}
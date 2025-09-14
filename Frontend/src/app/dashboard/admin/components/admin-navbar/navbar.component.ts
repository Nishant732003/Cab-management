import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-navbar',
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

 getUserInitials(name?: string): string {
  if (!name) return '';
  const parts = name.trim().split(/\s+/);
  const first = parts[0]?.charAt(0) ?? '';
  const last = parts[parts.length - 1]?.charAt(0) ?? '';
  return (first + last).toUpperCase();
}

getRoleBadgeClass(role?: string): string {
  const r = (role || '').toLowerCase();
  switch (r) {
    case 'admin':  return 'role-badge role-admin';
    case 'driver': return 'role-badge role-driver';
    case 'staff':  return 'role-badge role-staff';
    default:       return 'role-badge role-default';
  }
 
}

}

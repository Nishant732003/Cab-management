import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'user-navbar',
  standalone: false,
  templateUrl: './user-navbar.component.html',
  styleUrls: ['./user-navbar.component.css']
})
export class NavbarComponent implements OnInit {
  user: any = null;
  isAuthenticated = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadUser();
  }

  private loadUser(): void {
    try {
      const raw = localStorage.getItem('currentUser');
      this.user = raw ? JSON.parse(raw) : null;
      this.isAuthenticated = !!this.user;
    } catch {
      this.user = null;
      this.isAuthenticated = false;
    }
  }

  // Simple: get role name in Title Case, fallback to 'User'
  getRoleName(): string {
    const role = this.user?.role ?? 'user';
    return role
      .toString()
      .replace(/[_-]+/g, ' ')
      .replace(/\w\S*/g, (w: string) => w.toUpperCase() + w.slice(1).toLowerCase());
  }

  handleLogout(event: Event): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authToken');
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  getUserInitials(userName?: string): string {
    if (!userName) return 'D';
    return userName.split(' ').map(n => n.charAt(0)).join('').toUpperCase().slice(0, 2);
  }
}

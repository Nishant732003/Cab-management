// role-selection.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-selection',
  standalone:false,
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css']
})
export class RoleSelectionComponent {

  constructor(private router: Router) {}

  selectRole(role: 'admin' | 'user' | 'driver') {
    console.log('Selected role:', role);
    
    if (role === 'admin') {
      // Admin goes directly to login
      this.router.navigate(['/login'], { queryParams: { role: 'admin' } });
    } else {
      // User and Driver go to auth selection page
      this.router.navigate(['/auth-selection'], { queryParams: { role: role } });
    }
  }
}
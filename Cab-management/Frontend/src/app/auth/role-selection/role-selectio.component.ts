import { Component } from '@angular/core';
// import { Router } from '@angular/router';
import { Router, RouterLink } from '@angular/router'; // <--- Import RouterLink
import { CommonModule } from '@angular/common'; // <--- Import CommonModule


@Component({
  selector: 'app-role-selection',
  standalone: true, // <--- 1. Change to true ----added by me
  imports: [CommonModule, RouterLink], // <--- 2. Add this line -----added by me
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
    } else if (role === 'user') {
      // User goes to register user page
      this.router.navigate(['/registeruser'], { queryParams: { role: 'user' } });
    } else if (role === 'driver') {
      // Driver goes to register driver page
      this.router.navigate(['/registerdriver'], { queryParams: { role: 'driver' } });
    }
  }

  // Add this method to prevent event bubbling when clicking buttons
  onCardClick(event: Event, role: 'admin' | 'user' | 'driver') {
    event.preventDefault();
    this.selectRole(role);
  }
}
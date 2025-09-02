import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Define an interface for the driver object to fix typing errors
interface Driver {
  id: number;
  name: string;
  phone: string;
  email: string;
  vehicle: string;
  rating: number;
  status: string;
}

@Component({
  selector: 'app-driver',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.css']
})
export class DriverComponent {
  // FIX: Added missing properties that the template needs
  activeDriversCount: number = 0;
  averageRating: number = 0;
  totalTrips: number = 0;
  
  // FIX: Added a placeholder for the drivers array with the correct type
  drivers: Driver[] = []; 
  
  // FIX: Added the Math property so the template can use it for rounding
  Math = Math; 

  // FIX: Added the trackBy function required by the template's *ngFor loop
  trackByDriverId(index: number, driver: Driver): number {
    return driver.id;
  }
  
  getStatusClass(status: string): string {
    switch (status) {
      case 'Active': return 'bg-green-100 text-green-800';
      case 'Pending': return 'bg-yellow-100 text-yellow-800';
      case 'Inactive': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }
}
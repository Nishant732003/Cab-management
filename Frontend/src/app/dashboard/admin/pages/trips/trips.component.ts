import { Component } from '@angular/core';
import { AdminService, TripBooking } from '../../../../core/services/admin.service';
import { CommonModule } from '@angular/common'; // <-- IMPORT THIS
import { FormsModule } from '@angular/forms'; // <-- AND THIS

@Component({
  selector: 'app-trips',
  standalone: true, // <-- ADD THIS
  imports: [CommonModule, FormsModule], // <-- AND THIS
  templateUrl: './trips.component.html',
  styleUrls: ['./trips.component.css']
})
export class TripsComponent {
  trips: TripBooking[] = [];
  errorMessage: string = '';
  driverId: number | null = null;
  tripDate: string = '';

  constructor(private adminService: AdminService) {}

  fetchTripsByDriver() {
    if (this.driverId === null || this.driverId <= 0) {
      this.errorMessage = 'Please enter a valid Driver ID.';
      return;
    }
    this.errorMessage = '';
    this.adminService.getTripsByDriver(this.driverId).subscribe({
      next: (data) => { this.trips = data; },
      error: (err) => {
        this.errorMessage = 'Failed to fetch trips for the driver.';
        console.error(err);
      }
    });
  }

  fetchTripsByDate() {
    if (!this.tripDate) {
      this.errorMessage = 'Please select a date.';
      return;
    }
    this.errorMessage = '';
    this.adminService.getTripsByDate(this.tripDate).subscribe({
      next: (data) => { this.trips = data; },
      error: (err) => {
        this.errorMessage = 'Failed to fetch trips for the selected date.';
        console.error(err);
      }
    });
  }
}
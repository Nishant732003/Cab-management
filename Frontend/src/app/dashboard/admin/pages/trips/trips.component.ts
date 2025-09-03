import { Component, OnInit } from '@angular/core'; // Import OnInit
import { AdminService, TripBooking } from '../../../../core/services/admin.service';
import { CommonModule } from '@angular/common'; // <-- IMPORT THIS
import { FormsModule } from '@angular/forms'; // <-- AND THIS
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-trips',
  standalone: true, // <-- ADD THIS
  imports: [CommonModule, FormsModule], // <-- AND THIS
  templateUrl: './trips.component.html',
  styleUrls: ['./trips.component.css']
})
export class TripsComponent implements OnInit {
  trips: TripBooking[] = [];
  isLoading = true; // --- ADDED: Loading state ---
  errorMessage: string = '';
  // --- Form-bound properties for the search inputs ---
  driverId: number | null = null;
  tripDate: string = '';

  constructor(private adminService: AdminService) {}
   // --- ADDED: ngOnInit to load initial data ---
  ngOnInit(): void {
    // Set today's date as the default and fetch trips for today
    this.tripDate = new Date().toISOString().split('T')[0];
    this.fetchTripsByDate();
  }

  fetchTripsByDriver() {
    if (!this.driverId || this.driverId <= 0) {
      this.errorMessage = 'Please enter a valid Driver ID.';
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getTripsByDriver(this.driverId).pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
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
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getTripsByDate(this.tripDate).pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
      next: (data) => { this.trips = data; },
      error: (err) => {
        this.errorMessage = 'Failed to fetch trips for the selected date.';
        console.error(err);
      }
    });
  }
}

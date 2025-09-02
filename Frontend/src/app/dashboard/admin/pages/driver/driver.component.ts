import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, Driver } from '../../../../core/services/admin.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-driver',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.css']
})
export class DriverComponent implements OnInit {
  
  // State for the component
  allDrivers: Driver[] = []; // Stores the complete list of drivers
  filteredDrivers: Driver[] = []; // The list of drivers currently being displayed
  isLoading = true;
  errorMessage = '';
  currentView: 'all' | 'pending' = 'all'; // Controls the active filter
  verificationStatus: { [key: number]: 'VERIFYING' | 'SUCCESS' | 'ERROR' } = {};

  // Stats for the summary cards
  activeDriversCount = 0;
  pendingDriversCount = 0;
  averageRating = 0;

  Math = Math; // Expose Math object to template

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadAllDrivers();
  }

  /**
   * Fetches all drivers and updates the view.
   */
  loadAllDrivers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getAllDrivers().pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
      next: (data) => {
        this.allDrivers = data;
        this.applyFilter(); // Apply the current view filter
        this.calculateStats();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load driver data.';
        console.error('Error fetching drivers:', error);
      }
    });
  }

  /**
   * Applies the current filter to the driver list.
   */
  applyFilter(): void {
    if (this.currentView === 'pending') {
      this.filteredDrivers = this.allDrivers.filter(d => !d.verified);
    } else {
      this.filteredDrivers = [...this.allDrivers];
    }
  }

  /**
   * Sets the current view and applies the filter.
   * @param view The new view to apply ('all' or 'pending').
   */
  setView(view: 'all' | 'pending'): void {
    this.currentView = view;
    this.applyFilter();
  }

  /**
   * Calculates summary stats for the cards.
   */
  calculateStats(): void {
    this.activeDriversCount = this.allDrivers.filter(d => d.verified).length;
    this.pendingDriversCount = this.allDrivers.length - this.activeDriversCount;

    if (this.allDrivers.length > 0) {
      const totalRating = this.allDrivers.reduce((sum, driver) => sum + (driver.rating || 0), 0);
      this.averageRating = totalRating / this.allDrivers.length;
    } else {
      this.averageRating = 0;
    }
  }

  /**
   * Handles the verification of a driver.
   * @param driverId The ID of the driver to verify.
   */
  verifyDriver(driverId: number): void {
    this.verificationStatus[driverId] = 'VERIFYING';
    this.adminService.verifyDriver(driverId).pipe(
      finalize(() => {
        // We can remove the status after a delay, or just leave it as success
      })
    ).subscribe({
      next: (verifiedDriver: Driver) => {
        this.verificationStatus[driverId] = 'SUCCESS';
        // Update the driver's status in our main list to reflect the change immediately
        const driverIndex = this.allDrivers.findIndex(d => d.userId === driverId);
        if (driverIndex > -1) {
          this.allDrivers[driverIndex].verified = true;
        }
        this.applyFilter(); // Re-apply the filter, which will remove the driver from the 'pending' view
        this.calculateStats(); // Recalculate the stats cards
      },
      error: (error) => {
        this.verificationStatus[driverId] = 'ERROR';
        alert(`Failed to verify driver. Please check the console and try again.`);
        console.error(`Error verifying driver ${driverId}:`, error);
      }
    });
  }

  getStatusClass(verified: boolean): string {
    return verified ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800';
  }

  trackByDriverId(index: number, driver: Driver): number {
    return driver.userId;
  }
}


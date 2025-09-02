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
  
  drivers: Driver[] = [];
  isLoading = true;
  errorMessage = '';

  activeDriversCount = 0;
  averageRating = 0;
  totalTrips = 0; // This remains a placeholder for now

  Math = Math;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadDrivers();
  }

  loadDrivers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.getAllDrivers().pipe(
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (data) => {
        this.drivers = data;
        this.calculateStats();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load driver data. Please try again later.';
        console.error('Error fetching drivers:', error);
      }
    });
  }

  calculateStats(): void {
    if (!this.drivers || this.drivers.length === 0) {
      this.activeDriversCount = 0;
      this.averageRating = 0;
      return;
    }
    this.activeDriversCount = this.drivers.filter(d => d.verified).length;
    const totalRating = this.drivers.reduce((sum, driver) => sum + (driver.rating || 0), 0);
    this.averageRating = this.drivers.length > 0 ? totalRating / this.drivers.length : 0;
  }

  refreshData(): void {
    this.loadDrivers();
  }

  getStatusClass(verified: boolean): string {
    return verified ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800';
  }
  
  trackByDriverId(index: number, driver: Driver): number {
    return driver.userId;
  }
}


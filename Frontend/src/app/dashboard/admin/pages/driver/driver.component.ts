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
  allDrivers: Driver[] = [];
  filteredDrivers: Driver[] = [];
  isLoading = true;
  errorMessage = '';
  currentView: 'all' | 'pending' = 'all';
  verificationStatus: { [key: number]: 'VERIFYING' | 'SUCCESS' | 'ERROR' } = {};

  // Stats
  activeDriversCount = 0;
  pendingDriversCount = 0;
  averageRating = 0;

  // Delete modal state (same pattern as User)
  showDeleteModal = false;
  driverToDelete: Driver | null = null;
  isDeleting = false;

  Math = Math;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAllDrivers();
  }

  loadAllDrivers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getAllDrivers().pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
      next: (data) => {
        this.allDrivers = data ?? [];
        this.applyFilter();
        this.calculateStats();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load driver data.';
        console.error('Error fetching drivers:', error);
      }
    });
  }

  applyFilter(): void {
    if (this.currentView === 'pending') {
      this.filteredDrivers = this.allDrivers.filter(d => !d.verified);
    } else {
      this.filteredDrivers = [...this.allDrivers];
    }
  }

  setView(view: 'all' | 'pending'): void {
    this.currentView = view;
    this.applyFilter();
  }

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

  verifyDriver(driverId: number): void {
    this.verificationStatus[driverId] = 'VERIFYING';
    this.adminService.verifyDriver(driverId).pipe(
      finalize(() => {})
    ).subscribe({
      next: (message: string) => {
        console.log('Backend says:', message);
        this.verificationStatus[driverId] = 'SUCCESS';

        const driverIndex = this.allDrivers.findIndex(d => d.userId === driverId);
        if (driverIndex > -1) {
          this.allDrivers[driverIndex].verified = true;
        }
        this.applyFilter();
        this.calculateStats();
      },
      error: (error) => {
        this.verificationStatus[driverId] = 'ERROR';
        alert(`Failed to verify driver. Please check the console and try again.`);
        console.error(`Error verifying driver ${driverId}:`, error);
      }
    });
  }

  // Delete modal handlers (mirrors User component)
  openDeleteModal(driver: Driver): void {
    this.driverToDelete = driver;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.driverToDelete = null;
  }

  confirmDelete(): void {
    if (!this.driverToDelete?.username) {
      alert('Username is missing for the selected driver.');
      return;
    }

    const username = this.driverToDelete.username;
    const displayName =
      `${this.driverToDelete?.firstName ?? ''} ${this.driverToDelete?.lastName ?? ''}`.trim() ||
      username;

    this.isDeleting = true;

    // Use the same API as users to delete by username
    this.adminService.deleteUser(username).pipe(
      finalize(() => { this.isDeleting = false; })
    ).subscribe({
      next: () => {
        // Remove by username to mirror server identifier
        this.allDrivers = this.allDrivers.filter(d => d.username !== username);
        this.applyFilter();
        this.calculateStats();
        this.closeDeleteModal();
        alert(`Driver ${displayName} has been deleted successfully.`);
      },
      error: (err) => {
        console.error('Driver delete failed:', err);
        alert('Failed to delete the driver. Please try again.');
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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Location, TitleCasePipe } from '@angular/common';
// import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { DriverAuthContext } from '../../../../redux/context/DriverAuthContext';
import { DriverUser } from '../../../../redux/slice/driverAuthslice';

@Component({
  selector: 'driver-navbar',
  standalone: false, 
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Component state properties
  user: DriverUser | null = null;
  isAuthenticated = false;
  isLoading = false;
  error: string | null = null;
  searchQuery = '';
  mobileSearchQuery = '';
  isMobileSidebarOpen = false;
  
  constructor(
    private driverAuthContext: DriverAuthContext,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.subscribeToAuthState();
    this.initializeAuth();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Subscribe to all authentication state changes
   */
  private subscribeToAuthState(): void {
    // Subscribe to user changes
    this.driverAuthContext.user$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      this.user = user;
    });

    // Subscribe to authentication status
    this.driverAuthContext.isAuthenticated$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });

    // Subscribe to loading state
    this.driverAuthContext.isLoading$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isLoading => {
      this.isLoading = isLoading;
    });

    // Subscribe to error state
    this.driverAuthContext.error$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(error => {
      this.error = error;
    });
  }

  /**
   * Initialize authentication from localStorage
   */
  private initializeAuth(): void {
    this.driverAuthContext.initializeAuth();
    
    // If no user found after initialization, set mock user for demo
    setTimeout(() => {
      if (!this.driverAuthContext.user) {
        const mockUser: DriverUser = {
          id: 'driver-1',
          userName: 'John Driver',
          email: 'john.driver@example.com',
          phone: '+1-555-0123',
          licenseNumber: 'DL123456789',
          vehicle: {
            make: 'Toyota',
            model: 'Camry',
            year: 2020,
            plateNumber: 'ABC123'
          },
          isOnline: false,
          rating: 4.8,
          totalTrips: 1247,
          totalEarnings: 45650.75
        };
        
        this.driverAuthContext.setUser(mockUser);
      }
    }, 100);
  }

  /**
   * Handle user logout
   */
  handleLogout(event: Event): void {
    event.preventDefault();
    this.driverAuthContext.handleLogout();
    this.showToast('Logged Out Successfully', 'You have been successfully logged out.');
  }

  /**
   * Handle driver online/offline status toggle
   */
  async toggleDriverStatus(): Promise<void> {
    try {
      await this.driverAuthContext.toggleOnlineStatus();
      const statusText = this.user?.isOnline ? 'online' : 'offline';
      this.showToast('Status Updated', `You are now ${statusText}.`);
    } catch (error) {
      this.showToast('Status Update Failed', 'Unable to update your status.', 'error');
    }
  }

  /**
   * Navigate to user profile
   */
  handleUserProfile(): void {
    this.driverAuthContext.router.navigate(['/driver/profile']);
  }

  /**
   * Navigate to driver earnings
   */
  handleEarnings(): void {
    this.driverAuthContext.router.navigate(['/driver/earnings']);
  }

  /**
   * Navigate to trip history
   */
  handleTripHistory(): void {
    this.driverAuthContext.router.navigate(['/driver/trips']);
  }

  /**
   * Navigate to vehicle management
   */
  handleVehicleInfo(): void {
    this.driverAuthContext.router.navigate(['/driver/vehicle']);
  }

  /**
   * Navigate back using Location service
   */
  handleBack(): void {
    this.location.back();
  }

  /**
   * Navigate to notifications
   */
  handleNotifications(): void {
    this.driverAuthContext.router.navigate(['/driver/notifications']);
  }

  /**
   * Navigate to settings
   */
  handleSettings(): void {
    this.driverAuthContext.router.navigate(['/driver/settings']);
  }

  /**
   * Toggle mobile sidebar visibility
   */
  toggleMobileSidebar(): void {
    this.isMobileSidebarOpen = !this.isMobileSidebarOpen;
  }

  /**
   * Close mobile sidebar
   */
  closeMobileSidebar(): void {
    this.isMobileSidebarOpen = false;
  }

  /**
   * Get current formatted date
   */
  getCurrentDate(): string {
    const now = new Date();
    return now.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Get current formatted time
   */
  getCurrentTime(): string {
    const now = new Date();
    return now.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Get driver status badge styling
   */
  getDriverStatusBadgeColor(): string {
    if (!this.user) return 'badge-offline';
    return this.user.isOnline ? 'badge-online' : 'badge-offline';
  }

  /**
   * Get driver status text
   */
  getDriverStatusText(): string {
    if (!this.user) return 'Offline';
    return this.user.isOnline ? 'Online' : 'Offline';
  }

  /**
   * Get driver rating display
   */
  getDriverRating(): string {
    if (!this.user) return '0.0';
    return this.user.rating.toFixed(1);
  }

  /**
   * Get formatted total earnings
   */
  getFormattedEarnings(): string {
    if (!this.user) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(this.user.totalEarnings);
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(userName?: string): string {
    if (!userName) return 'D';
    
    return userName
      .split(' ')
      .map(name => name.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  /**
   * Get vehicle display info
   */
  getVehicleInfo(): string {
    if (!this.user?.vehicle) return 'No vehicle';
    const { year, make, model } = this.user.vehicle;
    return `${year} ${make} ${model}`;
  }

  /**
   * Get vehicle plate number
   */
  getVehiclePlate(): string {
    return this.user?.vehicle?.plateNumber || 'N/A';
  }

  /**
   * Get license number
   */
  getLicenseNumber(): string {
    return this.user?.licenseNumber || 'N/A';
  }

  /**
   * Get total trips count
   */
  getTotalTrips(): number {
    return this.user?.totalTrips || 0;
  }

  /**
   * Check if driver is currently online
   */
  isDriverOnline(): boolean {
    return this.user?.isOnline || false;
  }

  /**
   * Clear any authentication errors
   */
  clearError(): void {
    this.driverAuthContext.clearError();
  }

  /**
   * Show toast notification (implement with your toast service)
   */
  private showToast(title: string, description: string, type: 'success' | 'error' | 'info' = 'success'): void {
    console.log(`Toast [${type}]: ${title} - ${description}`);
    // TODO: Implement with your actual toast service
    // Example: this.toastService.show({ title, description, type });
  }

  /**
   * Handle search functionality
   */
  onSearch(query: string): void {
    if (!query.trim()) {
      this.showToast('Search Error', 'Please enter a search term.', 'error');
      return;
    }
    
    console.log('Searching for:', query);
    // TODO: Implement actual search logic for driver-specific content
    // Example: this.driverAuthContext.router.navigate(['/driver/search'], { queryParams: { q: query.trim() } });
  }

  /**
   * Handle search input keypress events
   */
  onSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.onSearch(this.searchQuery);
    }
  }

  /**
   * Handle mobile search input keypress events
   */
  onMobileSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.onSearch(this.mobileSearchQuery);
      this.closeMobileSidebar(); // Close sidebar after search
    }
  }

  /**
   * Clear search queries
   */
  clearSearch(): void {
    this.searchQuery = '';
    this.mobileSearchQuery = '';
  }

  /**
   * Handle escape key to close mobile sidebar
   */
  onEscapeKey(event: KeyboardEvent): void {
    if (event.key === 'Escape' && this.isMobileSidebarOpen) {
      this.closeMobileSidebar();
    }
  }

  /**
   * Handle window resize to close mobile sidebar on larger screens
   */
  onWindowResize(): void {
    if (window.innerWidth >= 768 && this.isMobileSidebarOpen) {
      this.closeMobileSidebar();
    }
  }

  /**
   * Emergency contact handler
   */
  handleEmergencyContact(): void {
    // TODO: Implement emergency contact functionality
    console.log('Emergency contact initiated');
    this.showToast('Emergency Contact', 'Emergency services have been notified.', 'info');
  }

  /**
   * Quick actions for driver
   */
  quickStartTrip(): void {
    this.driverAuthContext.router.navigate(['/driver/trip/start']);
  }

  quickViewTrips(): void {
    this.driverAuthContext.router.navigate(['/driver/trips/active']);
  }

  quickViewEarnings(): void {
    this.driverAuthContext.router.navigate(['/driver/earnings/today']);
  }
}
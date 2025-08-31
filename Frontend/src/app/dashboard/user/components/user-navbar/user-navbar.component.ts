import { Component, OnInit, OnDestroy } from '@angular/core';
import { Location } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { UserAuthContext } from '../../../../redux/context/UserAuthContext';
import { User, ActiveRide } from '../../../../redux/slice/userAuthSlice';

@Component({
  selector: 'user-navbar',
  standalone: false,
  templateUrl: './user-navbar.component.html',
  styleUrls: ['./user-navbar.component.css']
})
export class UserNavbarComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Component state properties
  user: User | null = null;
  isAuthenticated = false;
  isLoading = false;
  error: string | null = null;
  searchQuery = '';
  mobileSearchQuery = '';
  isMobileSidebarOpen = false;
  activeRide: ActiveRide | null = null;
  currentLocation = 'Current Location';
  
  constructor(
    private userAuthContext: UserAuthContext,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.subscribeToAuthState();
    this.initializeAuth();
    this.getCurrentLocationFromBrowser();
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
    this.userAuthContext.user$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      this.user = user;
    });

    // Subscribe to authentication status
    this.userAuthContext.isAuthenticated$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });

    // Subscribe to loading state
    this.userAuthContext.isLoading$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isLoading => {
      this.isLoading = isLoading;
    });

    // Subscribe to error state
    this.userAuthContext.error$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(error => {
      this.error = error;
    });

    // Subscribe to active ride state
    this.userAuthContext.activeRide$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(activeRide => {
      this.activeRide = activeRide;
    });
  }

  /**
   * Initialize authentication from localStorage
   */
  private initializeAuth(): void {
    this.userAuthContext.initializeAuth();
    
    // If no user found after initialization, set mock user for demo
    setTimeout(() => {
      if (!this.userAuthContext.user) {
        const mockUser: User = {
          id: 'user-1',
          userName: 'John Smith',
          email: 'john.smith@example.com',
          phone: '+1-555-0123',
          profilePicture: null,
          rating: 4.9,
          totalRides: 87,
          isActive:true,
          isVerified:true,
          preferredPaymentMethod: 'card-ending-4532',
          favoriteLocations: [
            { id: '1', name: 'Home', address: '123 Main St, City, State' },
            { id: '2', name: 'Work', address: '456 Business Ave, City, State' }
          ],
          ridePreferences: {
            preferredRideType: 'economy',
            allowSharedRides: true,
            musicPreference: 'pop',
            temperaturePreference: 'cool'
          }
        };
        
        this.userAuthContext.setUser(mockUser);
      }
    }, 100);
  }

  /**
   * Get current location from browser geolocation API
   */
  private getCurrentLocationFromBrowser(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          // In a real app, you'd reverse geocode these coordinates
          this.currentLocation = 'Current Location';
          console.log('Location:', position.coords.latitude, position.coords.longitude);
        },
        (error) => {
          console.warn('Geolocation error:', error);
          this.currentLocation = 'Location unavailable';
        }
      );
    }
  }

  /**
   * Handle user logout
   */
  handleLogout(event: Event): void {
    event.preventDefault();
    this.userAuthContext.handleLogout();
    this.showToast('Logged Out Successfully', 'You have been successfully logged out.');
  }

  /**
   * Navigate to user profile
   */
  handleUserProfile(): void {
    this.userAuthContext.router.navigate(['/user/profile']);
  }

  /**
   * Navigate to ride history
   */
  handleRideHistory(): void {
    this.userAuthContext.router.navigate(['/user/rides']);
  }

  /**
   * Navigate to wallet/payment methods
   */
  handleWallet(): void {
    this.userAuthContext.router.navigate(['/user/wallet']);
  }

  /**
   * Navigate to favorite places
   */
  handleFavorites(): void {
    this.userAuthContext.router.navigate(['/user/favorites']);
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
    this.userAuthContext.router.navigate(['/user/notifications']);
  }

  /**
   * Navigate to settings
   */
  handleSettings(): void {
    this.userAuthContext.router.navigate(['/user/settings']);
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
   * Get current location display
   */
  getCurrentLocation(): string {
    return this.currentLocation;
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
   * Get user rating display
   */
  getUserRating(): string {
    if (!this.user) return '0.0';
    return this.user.rating.toFixed(1);
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(userName?: string): string {
    if (!userName) return 'U';
    
    return userName
      .split(' ')
      .map(name => name.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  /**
   * Get total rides count
   */
  getTotalRides(): number {
    return this.user?.totalRides || 0;
  }

  /**
   * Check if user has active ride
   */
  hasActiveRide(): boolean {
    return this.activeRide !== null;
  }

  /**
   * Get active ride status text
   */
  getActiveRideStatus(): string {
    if (!this.activeRide) return '';
    
    switch (this.activeRide.status) {
      case 'requested':
        return 'Finding Driver...';
      case 'accepted':
        return 'Driver Assigned';
      case 'driver_arriving':
        return 'Driver Arriving';
      case 'driver_arrived':
        return 'Driver Arrived';
      case 'in_progress':
        return 'In Progress';
      case 'completed':
        return 'Completed';
      default:
        return 'Active Ride';
    }
  }

  /**
   * Check if user has unread notifications
   */
  hasUnreadNotifications(): boolean {
    // This would be replaced with actual notification state from your service
    return Math.random() > 0.5; // Mock implementation
  }

  /**
   * Clear any authentication errors
   */
  clearError(): void {
    this.userAuthContext.clearError();
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
   * Handle destination search functionality
   */
  onSearch(query: string): void {
    if (!query.trim()) {
      this.showToast('Search Error', 'Please enter a destination.', 'error');
      return;
    }
    
    console.log('Searching for destination:', query);
    // TODO: Implement actual search logic for places/destinations
    // Example: this.userAuthContext.router.navigate(['/user/search'], { queryParams: { destination: query.trim() } });
    this.userAuthContext.router.navigate(['/user/book-ride'], { 
      queryParams: { destination: query.trim() } 
    });
  }

  onSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.onSearch(this.searchQuery);
    }
  }

  onMobileSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.onSearch(this.mobileSearchQuery);
      this.closeMobileSidebar();
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.mobileSearchQuery = '';
  }

  onEscapeKey(event: KeyboardEvent): void {
    if (event.key === 'Escape' && this.isMobileSidebarOpen) {
      this.closeMobileSidebar();
    }
  }

  onWindowResize(): void {
    if (window.innerWidth >= 768 && this.isMobileSidebarOpen) {
      this.closeMobileSidebar();
    }
  }

  handleEmergencyContact(): void {
    // TODO: Implement emergency contact functionality
    console.log('Emergency contact initiated');
    this.showToast('Emergency Contact', 'Emergency services have been notified.', 'info');
    
    // In a real app, this might:
    // - Share current location with emergency services
    // - Send emergency notifications to emergency contacts
    // - Alert the current driver if in an active ride
  }

  /**
   * Quick actions for user
   */
  quickBookRide(): void {
    this.userAuthContext.router.navigate(['/user/book-ride']);
  }

  quickViewRides(): void {
    this.userAuthContext.router.navigate(['/user/rides/active']);
  }

  quickViewWallet(): void {
    this.userAuthContext.router.navigate(['/user/wallet']);
  }

  /**
   * Handle ride booking from current location
   */
  bookRideFromCurrentLocation(): void {
    if (this.currentLocation === 'Location unavailable') {
      this.showToast('Location Error', 'Please enable location services to book a ride.', 'error');
      return;
    }
    
    this.userAuthContext.router.navigate(['/user/book-ride'], {
      queryParams: { pickup: this.currentLocation }
    });
  }

  /**
   * Get preferred payment method display
   */
  getPreferredPaymentMethod(): string {
    if (!this.user?.preferredPaymentMethod) return 'No payment method';
    return `****${this.user.preferredPaymentMethod.slice(-4)}`;
  }

  /**
   * Get favorite locations count
   */
  getFavoriteLocationsCount(): number {
    return this.user?.favoriteLocations?.length || 0;
  }

  /**
   * Handle ride cancellation (if user has active ride)
   */
  cancelActiveRide(): void {
    if (!this.activeRide) return;
    
    // TODO: Implement ride cancellation logic
    this.userAuthContext.cancelActiveRide(this.activeRide.id);
    this.showToast('Ride Cancelled', 'Your ride has been cancelled.');
  }

  /**
   * Get estimated arrival time for active ride
   */
  getEstimatedArrival(): string {
    if (!this.activeRide?.estimatedArrival) return '';
    
    const now = new Date();
    const arrival = new Date(this.activeRide.estimatedArrival);
    const diffMinutes = Math.ceil((arrival.getTime() - now.getTime()) / (1000 * 60));
    
    if (diffMinutes <= 0) return 'Arriving now';
    if (diffMinutes === 1) return '1 minute';
    return `${diffMinutes} minutes`;
  }

  /**
   * Handle sharing ride details
   */
  shareRideDetails(): void {
    if (!this.activeRide) return;
    
    const shareData = {
      title: 'My Ride Details',
      text: `I'm currently in a ride. Driver: ${this.activeRide.driverName}, ETA: ${this.getEstimatedArrival()}`,
      url: window.location.href
    };
    
    if (navigator.share && navigator.canShare(shareData)) {
      navigator.share(shareData).catch(err => console.log('Error sharing:', err));
    } else {
      // Fallback for browsers that don't support native sharing
      navigator.clipboard.writeText(`${shareData.text} - ${shareData.url}`);
      this.showToast('Copied to Clipboard', 'Ride details copied to clipboard.');
    }
  }

  /**
   * Handle adding current location to favorites
   */
  addCurrentLocationToFavorites(): void {
    if (this.currentLocation === 'Location unavailable') {
      this.showToast('Location Error', 'Current location is not available.', 'error');
      return;
    }
    
    // TODO: Implement add to favorites logic
    console.log('Adding current location to favorites:', this.currentLocation);
    this.showToast('Location Added', 'Current location added to favorites.');
  }

  /**
   * Handle updating ride preferences
   */
  updateRidePreferences(): void {
    this.userAuthContext.router.navigate(['/user/preferences']);
  }
}
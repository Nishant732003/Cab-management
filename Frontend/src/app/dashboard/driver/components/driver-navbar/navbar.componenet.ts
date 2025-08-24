import { Component, OnInit, OnDestroy } from '@angular/core';
import { Location, TitleCasePipe } from '@angular/common';
// import { FormsModule } from '@angular/forms';
import { Subject } from '../../../../../../node_modules/rxjs/dist/types';
import { takeUntil } from '../../../../../../node_modules/rxjs/dist/types/operators';
import { AdminStaffAuthContext } from '../../../../redux/context/AdminAuthContext';
import { AdminStaffUser } from '../../../../redux/slice/adminAuthslice';

@Component({
  selector: 'app-navbar',
  standalone: false, 
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Component state properties
  user: AdminStaffUser | null = null;
  isAuthenticated = false;
  isLoading = false;
  error: string | null = null;
  searchQuery = '';
  mobileSearchQuery = '';
  isMobileSidebarOpen = false;
  
  constructor(
    private adminAuthContext: AdminStaffAuthContext,
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
    this.adminAuthContext.user$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      this.user = user;
    });

    // Subscribe to authentication status
    this.adminAuthContext.isAuthenticated$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isAuthenticated => {
      this.isAuthenticated = isAuthenticated;
    });

    // Subscribe to loading state
    this.adminAuthContext.isLoading$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isLoading => {
      this.isLoading = isLoading;
    });

    // Subscribe to error state
    this.adminAuthContext.error$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(error => {
      this.error = error;
    });
  }

  /**
   * Initialize authentication from localStorage
   */
  private initializeAuth(): void {
    this.adminAuthContext.initializeAuth();
    
    // If no user found after initialization, set mock user for demo
    setTimeout(() => {
      if (!this.adminAuthContext.user) {
        const mockUser: AdminStaffUser = {
          id: '1',
          userName: 'John Doe',
          email: 'john.doe@example.com',
          userType: 'admin'
        };
        
        this.adminAuthContext.setUser(mockUser);
      }
    }, 100);
  }

  /**
   * Handle user logout
   */
  handleLogout(event: Event): void {
    event.preventDefault();
    this.adminAuthContext.handleLogout();
    this.showToast('Logged Out Successfully', 'You have been successfully logged out.');
  }

  /**
   * Navigate to user profile
   */
  handleUserProfile(): void {
    this.adminAuthContext.router.navigate(['/adminstaff/profile']);
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
    this.adminAuthContext.router.navigate(['/admin/notifications']);
  }

  /**
   * Navigate to settings
   */
  handleSettings(): void {
    this.adminAuthContext.router.navigate(['/admin/settings']);
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
   * Get user type badge styling based on user type
   */
  getUserTypeBadgeColor(userType?: string): string {
    switch (userType?.toLowerCase()) {
      case 'admin':
        return 'badge-admin';
      case 'staff':
        return 'badge-staff';
      default:
        return 'badge-default';
    }
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
   * Check if user has admin privileges
   */
  isAdmin(): boolean {
    return this.user?.userType === 'admin';
  }

  /**
   * Check if user has staff privileges
   */
  isStaff(): boolean {
    return this.user?.userType === 'staff';
  }

  /**
   * Clear any authentication errors
   */
  clearError(): void {
    this.adminAuthContext.clearError();
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
    // TODO: Implement actual search logic
    // Example: this.adminAuthContext.router.navigate(['/adminstaff/search'], { queryParams: { q: query.trim() } });
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
}
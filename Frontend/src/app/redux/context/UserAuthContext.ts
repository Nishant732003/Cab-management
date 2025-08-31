import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { ReduxStore } from '../store';
import { 
  loginUser, 
  logoutUser, 
  registerUser,
  setUser, 
  setToken,
  clearUserError, 
  updateUserStatus,
  updateUserProfile,
  requestPasswordReset,
  verifyUser,
  refreshToken,
  requestRide,
  cancelRide,
  addFavoriteLocation,
  updateRidePreferences,
  setActiveRide,
  updateActiveRideStatus,
  clearActiveRide,
  removeFavoriteLocation,
  User,
  ActiveRide,
  FavoriteLocation,
  RidePreferences
} from '../slice/userAuthSlice';

@Injectable({
  providedIn: 'root'
})
export class UserAuthContext {
  private userSubject = new BehaviorSubject<User | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  private errorSubject = new BehaviorSubject<string | null>(null);
  private tokenSubject = new BehaviorSubject<string | null>(null);
  private activeRideSubject = new BehaviorSubject<ActiveRide | null>(null);

  public user$ = this.userSubject.asObservable();
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public error$ = this.errorSubject.asObservable();
  public token$ = this.tokenSubject.asObservable();
  public activeRide$ = this.activeRideSubject.asObservable();

  constructor(
    private reduxStore: ReduxStore,
    public router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.subscribeToStore();
    this.initializeAuth();
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  // Subscribe to Redux store changes and update BehaviorSubjects
  private subscribeToStore(): void {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState();
      const userAuth = state.userAuth;
      
      // Update all subjects with current state
      this.userSubject.next(userAuth.user);
      this.isAuthenticatedSubject.next(userAuth.isAuthenticated);
      this.isLoadingSubject.next(userAuth.isLoading);
      this.errorSubject.next(userAuth.error);
      this.tokenSubject.next(userAuth.token);
      this.activeRideSubject.next(userAuth.activeRide);
    });
  }

  // Authentication methods
  handleLogin = async (email: string, password: string): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(loginUser({ email, password }));
      
      if (loginUser.fulfilled.match(result)) {
        // Save token to localStorage on successful login
        if (result.payload.token) {
          this.saveToken(result.payload.token);
        }
        this.router.navigate(['/user/dashboard']);
      } else {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('User login failed:', error);
      throw error;
    }
  };

  handleRegister = async (userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
  }): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(registerUser(userData));
      
      if (registerUser.fulfilled.match(result)) {
        // Save token to localStorage on successful registration
        if (result.payload.token) {
          this.saveToken(result.payload.token);
        }
        this.router.navigate(['/user/dashboard']);
      } else {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('User registration failed:', error);
      throw error;
    }
  };

  handleLogout = (): void => {
    this.reduxStore.dispatch(logoutUser());
    this.clearStorage();
    this.router.navigate(['/login']);
  };

  // User management methods
  setUser = (user: User): void => {
    this.reduxStore.dispatch(setUser(user));
    // Save to localStorage if in browser
    if (this.isBrowser()) {
      try {
        localStorage.setItem('user', JSON.stringify(user));
      } catch (error) {
        console.error('Failed to save user to localStorage:', error);
      }
    }
  };

  setToken = (token: string): void => {
    this.reduxStore.dispatch(setToken(token));
    this.saveToken(token);
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearUserError());
  };

  toggleActiveStatus = async (): Promise<void> => {
    const currentUser = this.user;
    if (currentUser) {
      const newStatus = !currentUser.isActive;
      try {
        await this.reduxStore.dispatch(updateUserStatus(newStatus));
      } catch (error) {
        console.error('Failed to update user status:', error);
        throw error;
      }
    }
  };

  updateProfile = async (updates: Partial<User>): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(updateUserProfile(updates));
      
      if (updateUserProfile.fulfilled.match(result)) {
        // Update localStorage with new user data
        if (this.isBrowser() && result.payload) {
          try {
            localStorage.setItem('user', JSON.stringify(result.payload));
          } catch (error) {
            console.error('Failed to update user in localStorage:', error);
          }
        }
      } else {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Failed to update user profile:', error);
      throw error;
    }
  };

  // Ride management methods
  requestRide = async (rideData: {
    pickupLocation: { address: string; coordinates: { lat: number; lng: number } };
    dropoffLocation: { address: string; coordinates: { lat: number; lng: number } };
    rideType: string;
    paymentMethod: string;
  }): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(requestRide(rideData));
      
      if (requestRide.fulfilled.match(result)) {
        // Optionally navigate to ride tracking page
        this.router.navigate(['/user/ride/tracking']);
      } else {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Ride request failed:', error);
      throw error;
    }
  };

  cancelActiveRide = async (rideId: string): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(cancelRide(rideId));
      
      if (cancelRide.fulfilled.match(result)) {
        // Optionally show cancellation confirmation
        console.log('Ride cancelled successfully');
      } else {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Ride cancellation failed:', error);
      throw error;
    }
  };

  updateActiveRideStatus = (status: ActiveRide['status']): void => {
    this.reduxStore.dispatch(updateActiveRideStatus({ status }));
  };

  setActiveRide = (ride: ActiveRide): void => {
    this.reduxStore.dispatch(setActiveRide(ride));
  };

  clearActiveRide = (): void => {
    this.reduxStore.dispatch(clearActiveRide());
  };

  // Favorite locations management
  addFavoriteLocation = async (location: Omit<FavoriteLocation, 'id'>): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(addFavoriteLocation(location));
      
      if (!addFavoriteLocation.fulfilled.match(result)) {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Failed to add favorite location:', error);
      throw error;
    }
  };

  removeFavoriteLocation = (locationId: string): void => {
    this.reduxStore.dispatch(removeFavoriteLocation(locationId));
  };

  // Ride preferences management
  updateRidePreferences = async (preferences: RidePreferences): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(updateRidePreferences(preferences));
      
      if (!updateRidePreferences.fulfilled.match(result)) {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Failed to update ride preferences:', error);
      throw error;
    }
  };

  updatePreferences = async (preferences: any): Promise<void> => {
    if (this.user) {
      await this.updateProfile({ preferences });
    }
  };

  // Password and verification methods
  requestPasswordReset = async (email: string): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(requestPasswordReset(email));
      
      if (!requestPasswordReset.fulfilled.match(result)) {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Password reset request failed:', error);
      throw error;
    }
  };

  verifyAccount = async (token: string): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(verifyUser(token));
      
      if (!verifyUser.fulfilled.match(result)) {
        throw new Error(result.payload as string);
      }
    } catch (error) {
      console.error('Account verification failed:', error);
      throw error;
    }
  };

  refreshAuthToken = async (): Promise<void> => {
    try {
      const result = await this.reduxStore.dispatch(refreshToken());
      
      if (refreshToken.fulfilled.match(result)) {
        // Update localStorage with new token
        if (result.payload.token) {
          this.saveToken(result.payload.token);
        }
      } else {
        // If refresh fails, logout user
        this.handleLogout();
        throw new Error('Session expired');
      }
    } catch (error) {
      console.error('Token refresh failed:', error);
      throw error;
    }
  };

  // Getters for current state - accessing userAuth slice directly
  get user(): User | null {
    const state = this.reduxStore.getState();
    return state.userAuth?.user || null;
  }

  get isAuthenticated(): boolean {
    const state = this.reduxStore.getState();
    return state.userAuth?.isAuthenticated || false;
  }

  get isLoading(): boolean {
    const state = this.reduxStore.getState();
    return state.userAuth?.isLoading || false;
  }

  get error(): string | null {
    const state = this.reduxStore.getState();
    return state.userAuth?.error || null;
  }

  get token(): string | null {
    const state = this.reduxStore.getState();
    return state.userAuth?.token || null;
  }

  get activeRide(): ActiveRide | null {
    const state = this.reduxStore.getState();
    return state.userAuth?.activeRide || null;
  }

  // Observable getters for reactive programming
  getUser(): Observable<User | null> {
    return this.user$;
  }

  getIsAuthenticated(): Observable<boolean> {
    return this.isAuthenticated$;
  }

  getIsLoading(): Observable<boolean> {
    return this.isLoading$;
  }

  getError(): Observable<string | null> {
    return this.error$;
  }

  getToken(): Observable<string | null> {
    return this.token$;
  }

  getActiveRide(): Observable<ActiveRide | null> {
    return this.activeRide$;
  }

  // Storage methods
  initializeAuth(): void {
    if (!this.isBrowser()) {
      return;
    }

    try {
      const userStr = localStorage.getItem('user');
      const token = localStorage.getItem('userToken');
      
      if (userStr && token) {
        const user = JSON.parse(userStr);
        // Set both user and token in Redux store
        this.reduxStore.dispatch(setUser(user));
        this.reduxStore.dispatch(setToken(token));
      }
    } catch (error) {
      console.error('Failed to initialize auth from localStorage:', error);
      this.clearStorage();
    }
  }

  private clearStorage(): void {
    if (!this.isBrowser()) {
      return;
    }

    try {
      localStorage.removeItem('user');
      localStorage.removeItem('userToken');
    } catch (error) {
      console.error('Failed to clear localStorage:', error);
    }
  }

  saveToken(token: string): void {
    if (this.isBrowser()) {
      try {
        localStorage.setItem('userToken', token);
      } catch (error) {
        console.error('Failed to save token to localStorage:', error);
      }
    }
  }

  getStoredToken(): string | null {
    if (!this.isBrowser()) {
      return null;
    }

    try {
      return localStorage.getItem('userToken');
    } catch (error) {
      console.error('Failed to get token from localStorage:', error);
      return null;
    }
  }

  // Utility methods
  isUserVerified(): boolean {
    return this.user?.isVerified ?? false;
  }

  isUserActive(): boolean {
    return this.user?.isActive ?? false;
  }

  getUserRole(): string {
    return this.user?.role ?? 'user';
  }

  hasPermission(permission: string): boolean {
    const role = this.getUserRole();
    // Implement your permission logic here
    if (role === 'admin') return true;
    if (role === 'premium') {
      // Premium users might have additional permissions
      const premiumPermissions = ['book_premium_rides', 'priority_support'];
      return premiumPermissions.includes(permission);
    }
    // Regular user permissions
    const userPermissions = ['book_rides', 'view_history', 'manage_profile'];
    return userPermissions.includes(permission);
  }

  // Method to check if token is expired
  isTokenExpired(): boolean {
    const token = this.token;
    if (!token) return true;
    
    try {
      // Implement JWT token expiration check here
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      console.error('Error checking token expiration:', error);
      return true;
    }
  }

  // Ride-specific utility methods
  hasActiveRide(): boolean {
    return this.activeRide !== null;
  }

  canCancelRide(): boolean {
    const ride = this.activeRide;
    if (!ride) return false;
    
    const cancellableStatuses = ['requested', 'accepted', 'driver_arriving'];
    return cancellableStatuses.includes(ride.status);
  }

  getRideStatusMessage(): string {
    const ride = this.activeRide;
    if (!ride) return '';

    switch (ride.status) {
      case 'requested':
        return 'Looking for a driver...';
      case 'accepted':
        return `Driver ${ride.driverName} is on the way`;
      case 'driver_arriving':
        return 'Your driver is arriving soon';
      case 'driver_arrived':
        return 'Your driver has arrived';
      case 'in_progress':
        return 'Trip in progress';
      case 'completed':
        return 'Trip completed';
      case 'cancelled':
        return 'Trip cancelled';
      default:
        return 'Unknown status';
    }
  }

  getEstimatedArrivalTime(): string {
    const ride = this.activeRide;
    if (!ride?.estimatedArrival) return '';

    const arrival = new Date(ride.estimatedArrival);
    const now = new Date();
    const diffMinutes = Math.ceil((arrival.getTime() - now.getTime()) / (1000 * 60));

    if (diffMinutes <= 0) return 'Arriving now';
    if (diffMinutes === 1) return '1 minute away';
    return `${diffMinutes} minutes away`;
  }

  // Payment and wallet methods
  updatePaymentMethod = async (paymentMethodId: string): Promise<void> => {
    await this.updateProfile({ preferredPaymentMethod: paymentMethodId });
  };

  getFormattedWalletBalance(): string {
    if (!this.user?.wallet) return '$0.00';
    
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: this.user.wallet.currency || 'USD'
    }).format(this.user.wallet.balance);
  }

  // Location and preferences helpers
  getFavoriteLocationByName(name: string): FavoriteLocation | undefined {
    return this.user?.favoriteLocations.find(loc => 
      loc.name.toLowerCase() === name.toLowerCase()
    );
  }

  getRecentDestinations(): FavoriteLocation[] {
    // This would typically come from ride history or a separate API
    // For now, return favorite locations as a placeholder
    return this.user?.favoriteLocations.slice(0, 5) || [];
  }

  // Emergency contact methods
  addEmergencyContact = async (contact: {
    name: string;
    phone: string;
    relationship: string;
  }): Promise<void> => {
    const currentContacts = this.user?.emergencyContacts || [];
    const newContact = {
      id: `emergency_${Date.now()}`,
      ...contact
    };
    
    await this.updateProfile({
      emergencyContacts: [...currentContacts, newContact]
    });
  };

  removeEmergencyContact = async (contactId: string): Promise<void> => {
    const currentContacts = this.user?.emergencyContacts || [];
    const updatedContacts = currentContacts.filter(contact => contact.id !== contactId);
    
    await this.updateProfile({
      emergencyContacts: updatedContacts
    });
  };

  // Notification preferences
  updateNotificationPreferences = async (notifications: {
    rideUpdates: boolean;
    promotions: boolean;
    newsletter: boolean;
  }): Promise<void> => {
    
    
  
  };
}
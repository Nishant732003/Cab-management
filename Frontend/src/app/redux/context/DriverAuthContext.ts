import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ReduxStore } from '../store';
import { 
  loginDriver, 
  logoutDriver, 
  setDriver, 
  clearDriverError, 
  updateDriverStatus,
  updateDriverProfile,
  DriverUser 
} from '../slice/driverAuthslice' // Make sure import path is correct

@Injectable({
  providedIn: 'root'
})
export class DriverAuthContext {
  // Observables for reactive updates
  private userSubject = new BehaviorSubject<DriverUser | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  private errorSubject = new BehaviorSubject<string | null>(null);

  public user$ = this.userSubject.asObservable();
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public error$ = this.errorSubject.asObservable();

  constructor(
    private reduxStore: ReduxStore,
    public router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.subscribeToStore();
  }

  // Check if we're in browser environment
  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  // Subscribe to Redux store changes
  private subscribeToStore(): void {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState().driverAuth;
      this.userSubject.next(state.user);
      this.isAuthenticatedSubject.next(state.isAuthenticated);
      this.isLoadingSubject.next(state.isLoading);
      this.errorSubject.next(state.error);
    });
  }

  // Context methods
  handleLogout = (): void => {
    this.reduxStore.dispatch(logoutDriver());
    this.clearStorage();
    this.router.navigate(['/login']);
  };

  handleLogin = async (email: string, password: string): Promise<void> => {
    try {
      await this.reduxStore.dispatch(loginDriver({ email, password }));
      this.router.navigate(['/driver/overview']);
    } catch (error) {
      console.error('Driver login failed:', error);
    }
  };

  setUser = (user: DriverUser): void => {
    this.reduxStore.dispatch(setDriver(user));
    // Save to localStorage if in browser
    if (this.isBrowser()) {
      try {
        localStorage.setItem('driverUser', JSON.stringify(user));
      } catch (error) {
        console.error('Failed to save user to localStorage:', error);
      }
    }
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearDriverError());
  };

  toggleOnlineStatus = async (): Promise<void> => {
    if (this.user) {
      const newStatus = !this.user.isOnline;
      await this.reduxStore.dispatch(updateDriverStatus(newStatus));
    }
  };

  updateProfile = (updates: Partial<DriverUser>): void => {
    this.reduxStore.dispatch(updateDriverProfile(updates));
  };

  // Getters for current state - Fixed to use Redux store selectors
  get user(): DriverUser | null {
    return this.reduxStore.selectDriverUser();
  }

  get isAuthenticated(): boolean {
    return this.reduxStore.selectDriverIsAuthenticated();
  }

  get isLoading(): boolean {
    return this.reduxStore.selectDriverIsLoading();
  }

  get error(): string | null {
    return this.reduxStore.selectDriverError();
  }

  // Initialize auth from localStorage - Fixed with browser check
  initializeAuth(): void {
    // Only attempt to access localStorage in browser environment
    if (!this.isBrowser()) {
      return;
    }

    try {
      const userStr = localStorage.getItem('driverUser');
      const token = localStorage.getItem('driverToken');
      
      if (userStr && token) {
        const user = JSON.parse(userStr);
        this.setUser(user);
      }
    } catch (error) {
      console.error('Failed to initialize auth from localStorage:', error);
      this.clearStorage();
    }
  }

  // Clear storage - Fixed with browser check
  private clearStorage(): void {
    if (!this.isBrowser()) {
      return;
    }

    try {
      localStorage.removeItem('driverUser');
      localStorage.removeItem('driverToken');
    } catch (error) {
      console.error('Failed to clear localStorage:', error);
    }
  }

  // Helper method to save token
  saveToken(token: string): void {
    if (this.isBrowser()) {
      try {
        localStorage.setItem('driverToken', token);
      } catch (error) {
        console.error('Failed to save token to localStorage:', error);
      }
    }
  }

  // Helper method to get token
  getToken(): string | null {
    if (!this.isBrowser()) {
      return null;
    }

    try {
      return localStorage.getItem('driverToken');
    } catch (error) {
      console.error('Failed to get token from localStorage:', error);
      return null;
    }
  }
}
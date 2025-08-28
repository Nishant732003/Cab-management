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
  User
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

  public user$ = this.userSubject.asObservable();
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public error$ = this.errorSubject.asObservable();
  public token$ = this.tokenSubject.asObservable();

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
    phone?: string;
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

  // Storage methods
  private initializeAuth(): void {
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
    // Add more role-based permissions as needed
    return false;
  }

  // Method to check if token is expired (you'll need to implement token validation)
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
}
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from '../../../../node_modules/rxjs/dist/types';
import { ReduxStore } from '../store';
import { loginUser, logoutUser, setUser, clearError, AdminStaffUser } from '../slice/adminAuthslice'

@Injectable({
  providedIn: 'root'
})
export class AdminStaffAuthContext {
  // Observables for reactive updates
  private userSubject = new BehaviorSubject<AdminStaffUser | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  private errorSubject = new BehaviorSubject<string | null>(null);

  public user$ = this.userSubject.asObservable();
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public error$ = this.errorSubject.asObservable();

  constructor(
    private reduxStore: ReduxStore,
    public router: Router
  ) {
    this.subscribeToStore();
  }

  // Subscribe to Redux store changes
  private subscribeToStore(): void {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState().adminstaffauth;
      this.userSubject.next(state.user);
      this.isAuthenticatedSubject.next(state.isAuthenticated);
      this.isLoadingSubject.next(state.isLoading);
      this.errorSubject.next(state.error);
    });
  }

  // Context methods (like React useContext)
  handleLogout = (): void => {
    this.reduxStore.dispatch(logoutUser());
    this.router.navigate(['/login']);
  };

  handleLogin = async (email: string, password: string): Promise<void> => {
    try {
      await this.reduxStore.dispatch(loginUser({ email, password }));
      this.router.navigate(['/adminstaff/dashboard']);
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  setUser = (user: AdminStaffUser): void => {
    this.reduxStore.dispatch(setUser(user));
    // Store in localStorage for persistence
    localStorage.setItem('adminUser', JSON.stringify(user));
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearError());
  };

  // Getters for current state (like useSelector in React)
  get user(): AdminStaffUser | null {
    return this.reduxStore.selectUser();
  }

  get isAuthenticated(): boolean {
    return this.reduxStore.selectIsAuthenticated();
  }

  get isLoading(): boolean {
    return this.reduxStore.selectIsLoading();
  }

  get error(): string | null {
    return this.reduxStore.selectError();
  }

  // Initialize auth from localStorage
  initializeAuth(): void {
    const userStr = localStorage.getItem('adminUser');
    const token = localStorage.getItem('adminToken');
    
    if (userStr && token) {
      try {
        const user = JSON.parse(userStr);
        this.setUser(user);
      } catch (error) {
        console.error('Failed to parse stored user:', error);
        localStorage.removeItem('adminUser');
        localStorage.removeItem('adminToken');
      }
    }
  }
}
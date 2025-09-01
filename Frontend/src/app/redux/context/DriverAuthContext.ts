// Frontend/src/app/redux/context/DriverAuthContext.ts

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ReduxStore } from '../store';
import { DriverUser, resetDriverAuth, clearDriverError, updateDriverProfile } from '../slice/driverAuthslice';

@Injectable({
  providedIn: 'root'
})
export class DriverAuthContext {
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
    public router: Router // Add router back
  ) {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState().driver;
      this.userSubject.next(state.user);
      this.isAuthenticatedSubject.next(state.isAuthenticated);
      this.isLoadingSubject.next(state.isLoading);
      this.errorSubject.next(state.error);
    });
  }

  // --- FIX: Restore methods needed by components ---
  initializeAuth(): void {}

  handleLogout = (): void => {
    this.reduxStore.dispatch(resetDriverAuth());
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearDriverError());
  };

  toggleOnlineStatus = async (): Promise<void> => {
    const currentUser = this.user;
    if (currentUser) {
      const newStatus = !currentUser.isOnline;
      this.reduxStore.dispatch(updateDriverProfile({ isOnline: newStatus }));
    }
  };

  setUser = (user: DriverUser): void => {
    this.reduxStore.dispatch(updateDriverProfile(user));
  };

  get user(): DriverUser | null {
    return this.reduxStore.getState().driver.user;
  }
}
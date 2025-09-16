// Frontend/src/app/redux/context/UserAuthContext.ts

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ReduxStore } from '../store';
import { User, ActiveRide, userLogout, clearUserError } from '../slice/userAuthSlice';

@Injectable({
  providedIn: 'root'
})
export class UserAuthContext {
  private userSubject = new BehaviorSubject<User | null>(null);
  private activeRideSubject = new BehaviorSubject<ActiveRide | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  private errorSubject = new BehaviorSubject<string | null>(null);

  public user$ = this.userSubject.asObservable();
  public activeRide$ = this.activeRideSubject.asObservable();
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public error$ = this.errorSubject.asObservable();

  constructor(
    private reduxStore: ReduxStore,
    public router: Router // Add router back
  ) {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState().user;
      this.userSubject.next(state.user);
      this.activeRideSubject.next(state.activeRide);
      this.isAuthenticatedSubject.next(state.isAuthenticated);
      this.isLoadingSubject.next(state.isLoading);
      this.errorSubject.next(state.error);
    });
  }

  // --- FIX: Restore methods needed by components ---
  initializeAuth(): void {}

  handleLogout = (): void => {
    this.reduxStore.dispatch(userLogout());
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearUserError());
  };

  setUser = (user: User): void => {
    // This is used for mock data, can be empty for now
  };

  cancelActiveRide = (rideId: string): void => {
    console.log(`Cancelling ride ${rideId}`);
  };

  get user(): User | null {
    return this.reduxStore.getState().user.user;
  }

  get activeRide(): ActiveRide | null {
    return this.reduxStore.getState().user.activeRide;
  }
}
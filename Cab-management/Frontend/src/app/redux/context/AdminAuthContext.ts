// Frontend/src/app/redux/context/AdminAuthContext.ts

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ReduxStore } from '../store';
import { AdminStaffUser, resetAuth, clearError, setUser } from '../slice/adminAuthslice';

@Injectable({
  providedIn: 'root'
})
export class AdminStaffAuthContext {
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
    public router: Router // Add router back
  ) {
    this.reduxStore.subscribe(() => {
      const state = this.reduxStore.getState().admin;
      this.userSubject.next(state.user);
      this.isAuthenticatedSubject.next(state.isAuthenticated);
      this.isLoadingSubject.next(state.isLoading);
      this.errorSubject.next(state.error);
    });
  }

  // --- FIX: Restore methods needed by components ---
  initializeAuth(): void {}

  handleLogout = (): void => {
    this.reduxStore.dispatch(resetAuth());
  };

  clearError = (): void => {
    this.reduxStore.dispatch(clearError());
  };

  setUser = (user: AdminStaffUser): void => {
    this.reduxStore.dispatch(setUser(user));
  };

  get user(): AdminStaffUser | null {
    return this.reduxStore.getState().admin.user;
  }
}
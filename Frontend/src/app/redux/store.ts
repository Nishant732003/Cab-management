import { configureStore } from '@reduxjs/toolkit';
import { Injectable } from '@angular/core';
import adminAuthReducer, { AdminStaffAuthState } from './slice/adminAuthslice';
import driverAuthReducer, { DriverAuthState } from './slice/driverAuthslice';
import userAuthReducer, { UserAuthState } from './slice/userAuthSlice'; // Add userAuth import

// Root state interface - Updated to include all auth slices
export interface RootState {
  adminstaffauth: AdminStaffAuthState;
  driverAuth: DriverAuthState;
  userAuth: UserAuthState; // Add user auth state
}

// Configure Redux store with all reducers
const reduxStore = configureStore({
  reducer: {
    adminstaffauth: adminAuthReducer,
    driverAuth: driverAuthReducer,
    userAuth: userAuthReducer, // Add user auth reducer
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActionPaths: ['meta.arg', 'payload.timestamp'],
      },
    }),
  devTools: true,
});

export type AppDispatch = typeof reduxStore.dispatch;

// Angular service wrapper for Redux store
@Injectable({
  providedIn: 'root'
})
export class ReduxStore {
  private store = reduxStore;

  // Get current state
  getState(): RootState {
    return this.store.getState();
  }

  // Dispatch actions
  dispatch(action: any) {
    return this.store.dispatch(action);
  }

  // Subscribe to store changes
  subscribe(listener: () => void) {
    return this.store.subscribe(listener);
  }

  // Admin Auth Selectors
  selectUser() {
    return this.getState().adminstaffauth.user;
  }

  selectIsAuthenticated() {
    return this.getState().adminstaffauth.isAuthenticated;
  }

  selectIsLoading() {
    return this.getState().adminstaffauth.isLoading;
  }

  selectError() {
    return this.getState().adminstaffauth.error;
  }

  // Driver Auth Selectors
  selectDriverUser() {
    return this.getState().driverAuth.user;
  }

  selectDriverIsAuthenticated() {
    return this.getState().driverAuth.isAuthenticated;
  }

  selectDriverIsLoading() {
    return this.getState().driverAuth.isLoading;
  }

  selectDriverError() {
    return this.getState().driverAuth.error;
  }

  // User Auth Selectors - Add these new selectors
  selectUserUser() {
    return this.getState().userAuth.user;
  }

  selectUserIsAuthenticated() {
    return this.getState().userAuth.isAuthenticated;
  }

  selectUserIsLoading() {
    return this.getState().userAuth.isLoading;
  }

  selectUserError() {
    return this.getState().userAuth.error;
  }

  selectUserToken() {
    return this.getState().userAuth.token;
  }

  // Generic selectors for accessing any auth slice
  selectAuthState<T extends keyof RootState>(authType: T): RootState[T] {
    return this.getState()[authType];
  }
}

export const store = reduxStore;
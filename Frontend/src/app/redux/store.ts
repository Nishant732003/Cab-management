import { configureStore } from '@reduxjs/toolkit';
import { Injectable } from '@angular/core';
import adminAuthReducer, { AdminStaffAuthState } from './slice/adminAuthslice';
import driverAuthReducer, { DriverAuthState } from './slice/driverAuthslice'; // Add this import

// Root state interface - Updated to include both auth slices
export interface RootState {
  adminstaffauth: AdminStaffAuthState;
  driverAuth: DriverAuthState; // Add driver auth state
}

// Configure Redux store with both reducers
const reduxStore = configureStore({
  reducer: {
    adminstaffauth: adminAuthReducer,
    driverAuth: driverAuthReducer, // Add driver auth reducer
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

  // Driver Auth Selectors - Add these
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
}

export const store = reduxStore;

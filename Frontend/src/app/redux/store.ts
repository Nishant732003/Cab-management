import { configureStore } from '@reduxjs/toolkit';
import { Injectable } from '@angular/core';
import adminAuthReducer, { AdminStaffAuthState } from './slice/adminAuthslice';

// Root state interface
export interface RootState {
  adminstaffauth: AdminStaffAuthState;
}

// Configure Redux store - removed export to avoid redeclaration
const reduxStore = configureStore({
  reducer: {
    adminstaffauth: adminAuthReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Fixed: changed from ignoredActionsPaths to ignoredActionPaths
        ignoredActionPaths: ['meta.arg', 'payload.timestamp'],
      },
    }),
  devTools: true, // Enable Redux DevTools
});

export type AppDispatch = typeof reduxStore.dispatch;

// Angular service wrapper for Redux store
@Injectable({
  providedIn: 'root'
})
export class ReduxStore {
  private store = reduxStore; // Renamed to avoid confusion

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

  // Selectors
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
}

// Export the store instance if needed elsewhere
export const store = reduxStore;

// Remove duplicate export type declaration for RootState and AppDispatch
// (they're already exported above)
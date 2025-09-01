// Frontend/src/app/redux/store.ts

import { configureStore } from '@reduxjs/toolkit';
import userAuthReducer, { UserAuthState } from './slice/userAuthSlice';
import driverAuthReducer, { DriverAuthState } from './slice/driverAuthslice';
import adminAuthReducer, { AdminStaffAuthState } from './slice/adminAuthslice';

// --- FIX: Define the RootState based on the imported state shapes ---
export interface RootState {
  user: UserAuthState;
  driver: DriverAuthState;
  admin: AdminStaffAuthState;
  // This was missing from your context files, causing a crash
  adminstaffauth: AdminStaffAuthState; 
}

const store = configureStore({
  reducer: {
    user: userAuthReducer,
    driver: driverAuthReducer,
    admin: adminAuthReducer,
    adminstaffauth: adminAuthReducer, // Map the slice to the state key
  },
});

// This is a simplified wrapper to make the Redux store injectable
// You might need to adjust this depending on your full Redux setup
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ReduxStore {
  dispatch = store.dispatch;
  subscribe = store.subscribe;
  getState = store.getState;
  
  // Add selector methods that your old context files were trying to use
  selectUser = () => this.getState().user.user;
  selectIsAuthenticated = () => this.getState().user.isAuthenticated;
  selectIsLoading = () => this.getState().user.isLoading;
  selectError = () => this.getState().user.error;
  selectDriverUser = () => this.getState().driver.user;
  selectDriverIsAuthenticated = () => this.getState().driver.isAuthenticated;
  selectDriverIsLoading = () => this.getState().driver.isLoading;
  selectDriverError = () => this.getState().driver.error;
}

export default store;
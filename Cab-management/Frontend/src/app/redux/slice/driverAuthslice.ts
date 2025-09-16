// Frontend/src/app/redux/slice/driverAuthslice.ts

import { createSlice, PayloadAction } from '@reduxjs/toolkit';

// --- FIX: A complete DriverUser interface ---
export interface DriverUser {
  id: string;
  userName: string;
  email: string;
  phone: string;
  isOnline: boolean;
  licenseNumber: string;
  rating: number;
  totalEarnings: number;
  totalTrips: number;
  vehicle: {
      make: string;
      model: string;
      year: number;
      plateNumber: string;
  };
  role?: string;
}

export interface DriverAuthState {
  user: DriverUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  token: string | null;
}

const initialState: DriverAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  token: null,
};

const driverAuthSlice = createSlice({
  name: 'driverAuth',
  initialState,
  reducers: {
    driverLogin(state, action: PayloadAction<{ driver: DriverUser; token: string }>) {
      state.user = action.payload.driver;
      state.token = action.payload.token;
      state.isAuthenticated = true;
    },
    resetDriverAuth(state) {
      Object.assign(state, initialState);
    },
    clearDriverError(state) {
      state.error = null;
    },
    updateDriverProfile(state, action: PayloadAction<Partial<DriverUser>>) {
        if (state.user) {
            state.user = { ...state.user, ...action.payload };
        }
    }
  },
});

export const {
  driverLogin,
  resetDriverAuth,
  clearDriverError,
  updateDriverProfile
} = driverAuthSlice.actions;

export default driverAuthSlice.reducer;
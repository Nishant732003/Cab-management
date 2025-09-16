// Frontend/src/app/redux/slice/adminAuthslice.ts

import { createSlice, PayloadAction } from '@reduxjs/toolkit';

// --- FIX: Exporting all necessary interfaces ---
export interface AdminStaffUser {
  id: string;
  userName: string;
  email: string;
  userType: 'admin' | 'staff';
  role?: string;
}

export interface AdminStaffAuthState {
  user: AdminStaffUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  token: string | null;
}

const initialState: AdminStaffAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  token: null,
};

const adminAuthSlice = createSlice({
  name: 'adminAuth',
  initialState,
  reducers: {
    adminLogin(state, action: PayloadAction<{ admin: AdminStaffUser; token: string }>) {
      state.user = action.payload.admin;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      state.isLoading = false;
      state.error = null;
    },
    resetAuth(state) {
      Object.assign(state, initialState);
    },
    clearError(state) {
      state.error = null;
    },
    setUser(state, action: PayloadAction<AdminStaffUser>) {
        state.user = action.payload;
    }
  },
});

export const {
  adminLogin,
  resetAuth,
  clearError,
  setUser
} = adminAuthSlice.actions;

export default adminAuthSlice.reducer;
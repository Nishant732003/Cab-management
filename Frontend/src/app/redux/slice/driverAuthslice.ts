import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

// Types (same as before)
export interface DriverUser {
  id: string;
  userName: string;
  email: string;
  phone: string;
  licenseNumber: string;
  vehicle: {
    make: string;
    model: string;
    year: number;
    plateNumber: string;
  };
  isOnline: boolean;
  rating: number;
  totalTrips: number;
  totalEarnings: number;
}

export interface DriverAuthState {
  user: DriverUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

// Initial state
const initialState: DriverAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
};

// Async thunks
export const loginDriver = createAsyncThunk(
  'driverAuth/login',
  async (credentials: { email: string; password: string }) => {
    // Mock API call - replace with your actual API
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const user: DriverUser = {
      id: 'driver-1',
      userName: 'John Driver',
      email: credentials.email,
      phone: '+1-555-0123',
      licenseNumber: 'DL123456789',
      vehicle: {
        make: 'Toyota',
        model: 'Camry',
        year: 2020,
        plateNumber: 'ABC123'
      },
      isOnline: false,
      rating: 4.8,
      totalTrips: 1247,
      totalEarnings: 45650.75
    };
    
    const token = 'mock-driver-jwt-token-' + Date.now();
    
    // Store in localStorage (this is fine in async thunks)
    localStorage.setItem('driverToken', token);
    localStorage.setItem('driverUser', JSON.stringify(user));
    
    return { user, token };
  }
);

export const logoutDriver = createAsyncThunk(
  'driverAuth/logout',
  async () => {
    // Clear localStorage
    localStorage.removeItem('driverToken');
    localStorage.removeItem('driverUser');
    return null;
  }
);

export const updateDriverStatus = createAsyncThunk(
  'driverAuth/updateStatus',
  async (isOnline: boolean) => {
    await new Promise(resolve => setTimeout(resolve, 500));
    // Update localStorage after successful API call
    const userStr = localStorage.getItem('driverUser');
    if (userStr) {
      const user = JSON.parse(userStr);
      user.isOnline = isOnline;
      localStorage.setItem('driverUser', JSON.stringify(user));
    }
    return isOnline;
  }
);

// Slice
const driverAuthSlice = createSlice({
  name: 'driverAuth',
  initialState,
  reducers: {
    // Sync actions
    setDriver: (state, action: PayloadAction<DriverUser>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
      state.error = null;
    },
    clearDriverError: (state) => {
      state.error = null;
    },
    resetDriverAuth: () => initialState,
    setDriverLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    updateDriverProfile: (state, action: PayloadAction<Partial<DriverUser>>) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
        // Note: localStorage updates moved to async thunk or service layer
      }
    }
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginDriver.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginDriver.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(loginDriver.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Login failed';
        state.isAuthenticated = false;
        state.user = null;
      })
      
      // Logout
      .addCase(logoutDriver.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(logoutDriver.fulfilled, () => initialState)
      .addCase(logoutDriver.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Logout failed';
      })
      
      // Update status
      .addCase(updateDriverStatus.fulfilled, (state, action) => {
        if (state.user) {
          state.user.isOnline = action.payload;
        }
      });
  },
});

// Export actions and reducer
export const { 
  setDriver, 
  clearDriverError, 
  resetDriverAuth, 
  setDriverLoading, 
  updateDriverProfile 
} = driverAuthSlice.actions;

export default driverAuthSlice.reducer;
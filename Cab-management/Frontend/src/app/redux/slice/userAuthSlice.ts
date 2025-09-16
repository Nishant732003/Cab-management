import { createSlice, PayloadAction } from '@reduxjs/toolkit';

// --- FIX: Exporting all necessary interfaces that components depend on ---
export interface FavoriteLocation {
  id: string;
  name: string;
  address: string;
}

export interface RidePreferences {
  preferredRideType: string;
  allowSharedRides: boolean;
  musicPreference?: string;
  temperaturePreference?: 'cool' | 'warm' | 'auto'; // Add missing property
}

export interface ActiveRide {
  id: string;
  status: string;
  driverName: string;
  vehicleInfo: {
    make: string;
    model: string;
    plateNumber: string;
  };
  pickupLocation: { address: string };
  dropoffLocation: { address: string };
  estimatedArrival?: string;
  fare: { totalPrice: number };
}

export interface User {
  id: string;
  userName: string;
  email: string;
  phone: string;
  profilePicture?: string | null;
  isActive: boolean;
  isVerified: boolean;
  rating: number;
  totalRides: number;
  preferredPaymentMethod?: string;
  favoriteLocations: FavoriteLocation[];
  ridePreferences: RidePreferences;
  wallet?: {
    balance: number;
    currency: string;
  };
  emergencyContacts?: any[];
  preferences?: any;
  role?: string;
}

export interface UserAuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  token: string | null;
  activeRide: ActiveRide | null;
  rideHistory: ActiveRide[];
}

const initialState: UserAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  token: null,
  activeRide: null,
  rideHistory: [],
};

const userAuthSlice = createSlice({
  name: 'userAuth',
  initialState,
  reducers: {
    userLogin(state, action: PayloadAction<{ user: User; token: string }>) {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      state.isLoading = false;
      state.error = null;
    },
    userLogout(state) {
      Object.assign(state, initialState);
    },
    setActiveRide: (state, action: PayloadAction<ActiveRide>) => {
      state.activeRide = action.payload;
    },
    clearUserError: (state) => {
      state.error = null;
    },
  },
});

export const {
  userLogin,
  userLogout,
  setActiveRide,
  clearUserError
} = userAuthSlice.actions;

export default userAuthSlice.reducer;

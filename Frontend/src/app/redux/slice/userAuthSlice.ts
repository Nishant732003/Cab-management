import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

// Favorite location interface
export interface FavoriteLocation {
  id: string;
  name: string;
  address: string;
  coordinates?: {
    lat: number;
    lng: number;
  };
}

// Ride preferences interface
export interface RidePreferences {
  preferredRideType: 'economy' | 'comfort' | 'premium' | 'luxury';
  allowSharedRides: boolean;
  musicPreference?: 'pop' | 'rock' | 'jazz' | 'classical' | 'none';
  temperaturePreference?: 'cool' | 'warm' | 'auto';
  accessibility?: {
    wheelchairAccessible: boolean;
    hearingAssistance: boolean;
    visualAssistance: boolean;
  };
}

// Active ride interface
export interface ActiveRide {
  id: string;
  status: 'requested' | 'accepted' | 'driver_arriving' | 'driver_arrived' | 'in_progress' | 'completed' | 'cancelled';
  driverId: string;
  driverName: string;
  driverPhone: string;
  driverRating: number;
  vehicleInfo: {
    make: string;
    model: string;
    year: number;
    color: string;
    plateNumber: string;
  };
  pickupLocation: {
    address: string;
    coordinates: { lat: number; lng: number };
  };
  dropoffLocation: {
    address: string;
    coordinates: { lat: number; lng: number };
  };
  estimatedArrival?: string;
  estimatedDuration?: number; // in minutes
  fare: {
    basePrice: number;
    distance: number;
    duration: number;
    totalPrice: number;
    currency: string;
  };
  paymentMethod: string;
  requestedAt: string;
  acceptedAt?: string;
  completedAt?: string;
}

// Updated User interface for ride-sharing app
export interface User {
  id: string;
  userName: string; // Combined display name
  email: string;
  firstName?: string;
  lastName?: string;
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
  emergencyContacts?: Array<{
    id: string;
    name: string;
    phone: string;
    relationship: string;
  }>;
  preferences?: {
    notifications: {
      rideUpdates: boolean;
      promotions: boolean;
      newsletter: boolean;
    };
    theme: 'light' | 'dark' | 'auto';
    language: string;
    [key: string]: any;
  };
  createdAt?: string;
  updatedAt?: string;
  lastLogin?: string;
  role?: 'user' | 'premium' | 'admin';
}

// Auth state interface
interface UserAuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  token: string | null;
  activeRide: ActiveRide | null;
  rideHistory: ActiveRide[];
}

// Root state interface
interface RootState {
  userAuth: UserAuthState;
}

// Initial state
const initialState: UserAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  token: null,
  activeRide: null,
  rideHistory: [],
};

// Async thunks for ride management
export const requestRide = createAsyncThunk(
  'userAuth/requestRide',
  async (rideData: {
    pickupLocation: { address: string; coordinates: { lat: number; lng: number } };
    dropoffLocation: { address: string; coordinates: { lat: number; lng: number } };
    rideType: string;
    paymentMethod: string;
  }, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch('/api/rides/request', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(rideData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Ride request failed');
      }

      const data = await response.json();
      return data.ride;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const cancelRide = createAsyncThunk(
  'userAuth/cancelRide',
  async (rideId: string, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch(`/api/rides/${rideId}/cancel`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Ride cancellation failed');
      }

      return rideId;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const addFavoriteLocation = createAsyncThunk(
  'userAuth/addFavoriteLocation',
  async (location: Omit<FavoriteLocation, 'id'>, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch('/api/user/favorites', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(location),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Failed to add favorite location');
      }

      const data = await response.json();
      return data.location;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const updateRidePreferences = createAsyncThunk(
  'userAuth/updateRidePreferences',
  async (preferences: RidePreferences, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch('/api/user/ride-preferences', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(preferences),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Failed to update preferences');
      }

      const data = await response.json();
      return data.preferences;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

// Existing async thunks (updated to match new User interface)
export const loginUser = createAsyncThunk(
  'userAuth/login',
  async (credentials: { email: string; password: string }, { rejectWithValue }) => {
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Login failed');
      }

      const data = await response.json();
      return data; // Should contain { user: User, token: string }
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const registerUser = createAsyncThunk(
  'userAuth/register',
  async (userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone: string;
  }, { rejectWithValue }) => {
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...userData,
          userName: `${userData.firstName} ${userData.lastName}`
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Registration failed');
      }

      const data = await response.json();
      return data;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const updateUserStatus = createAsyncThunk(
  'userAuth/updateStatus',
  async (isActive: boolean, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch('/api/user/status', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ isActive }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Status update failed');
      }

      const data = await response.json();
      return data.user;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const updateUserProfile = createAsyncThunk(
  'userAuth/updateProfile',
  async (updates: Partial<User>, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const token = state.userAuth.token;

      const response = await fetch('/api/user/profile', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(updates),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Profile update failed');
      }

      const data = await response.json();
      return data.user;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const requestPasswordReset = createAsyncThunk(
  'userAuth/requestPasswordReset',
  async (email: string, { rejectWithValue }) => {
    try {
      const response = await fetch('/api/auth/password-reset', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Password reset request failed');
      }

      const data = await response.json();
      return data.message;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const verifyUser = createAsyncThunk(
  'userAuth/verify',
  async (token: string, { rejectWithValue }) => {
    try {
      const response = await fetch('/api/auth/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ token }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Verification failed');
      }

      const data = await response.json();
      return data.user;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

export const refreshToken = createAsyncThunk(
  'userAuth/refreshToken',
  async (_, { getState, rejectWithValue }) => {
    try {
      const state = getState() as RootState;
      const currentToken = state.userAuth.token;

      const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${currentToken}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        return rejectWithValue(errorData.message || 'Token refresh failed');
      }

      const data = await response.json();
      return data;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Network error occurred');
    }
  }
);

// Create slice
const userAuthSlice = createSlice({
  name: 'userAuth',
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
      state.error = null;
    },
    setToken: (state, action: PayloadAction<string>) => {
      state.token = action.payload;
    },
    clearUserError: (state) => {
      state.error = null;
    },
    logoutUser: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      state.error = null;
      state.isLoading = false;
      state.activeRide = null;
      state.rideHistory = [];
    },
    resetUserState: () => initialState,
    setActiveRide: (state, action: PayloadAction<ActiveRide>) => {
      state.activeRide = action.payload;
    },
    updateActiveRideStatus: (state, action: PayloadAction<{ status: ActiveRide['status'] }>) => {
      if (state.activeRide) {
        state.activeRide.status = action.payload.status;
      }
    },
    clearActiveRide: (state) => {
      if (state.activeRide) {
        state.rideHistory.push(state.activeRide);
        state.activeRide = null;
      }
    },
    removeFavoriteLocation: (state, action: PayloadAction<string>) => {
      if (state.user) {
        state.user.favoriteLocations = state.user.favoriteLocations.filter(
          loc => loc.id !== action.payload
        );
      }
    },
  },
  extraReducers: (builder) => {
    // Login cases
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
        state.isAuthenticated = false;
      });

    // Register cases
    builder
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Request ride cases
    builder
      .addCase(requestRide.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(requestRide.fulfilled, (state, action) => {
        state.isLoading = false;
        state.activeRide = action.payload;
        state.error = null;
      })
      .addCase(requestRide.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Cancel ride cases
    builder
      .addCase(cancelRide.fulfilled, (state, action) => {
        if (state.activeRide && state.activeRide.id === action.payload) {
          state.activeRide.status = 'cancelled';
          setTimeout(() => {
            if (state.activeRide) {
              state.rideHistory.push(state.activeRide);
              state.activeRide = null;
            }
          }, 2000);
        }
      })
      .addCase(cancelRide.rejected, (state, action) => {
        state.error = action.payload as string;
      });

    // Add favorite location cases
    builder
      .addCase(addFavoriteLocation.fulfilled, (state, action) => {
        if (state.user) {
          state.user.favoriteLocations.push(action.payload);
        }
      })
      .addCase(addFavoriteLocation.rejected, (state, action) => {
        state.error = action.payload as string;
      });

    // Update ride preferences cases
    builder
      .addCase(updateRidePreferences.fulfilled, (state, action) => {
        if (state.user) {
          state.user.ridePreferences = action.payload;
        }
      })
      .addCase(updateRidePreferences.rejected, (state, action) => {
        state.error = action.payload as string;
      });

    // Update status cases
    builder
      .addCase(updateUserStatus.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(updateUserStatus.fulfilled, (state, action) => {
        state.isLoading = false;
        if (state.user) {
          state.user = { ...state.user, ...action.payload };
        }
        state.error = null;
      })
      .addCase(updateUserStatus.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Update profile cases
    builder
      .addCase(updateUserProfile.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(updateUserProfile.fulfilled, (state, action) => {
        state.isLoading = false;
        if (state.user) {
          state.user = { ...state.user, ...action.payload };
        }
        state.error = null;
      })
      .addCase(updateUserProfile.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Password reset cases
    builder
      .addCase(requestPasswordReset.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(requestPasswordReset.fulfilled, (state) => {
        state.isLoading = false;
        state.error = null;
      })
      .addCase(requestPasswordReset.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Verify user cases
    builder
      .addCase(verifyUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(verifyUser.fulfilled, (state, action) => {
        state.isLoading = false;
        if (state.user) {
          state.user = { ...state.user, ...action.payload };
        }
        state.error = null;
      })
      .addCase(verifyUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Refresh token cases
    builder
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(refreshToken.rejected, (state) => {
        state.user = null;
        state.token = null;
        state.isAuthenticated = false;
        state.error = 'Session expired';
      });
  },
});

// Export actions
export const {
  setUser,
  setToken,
  clearUserError,
  logoutUser,
  resetUserState,
  setActiveRide,
  updateActiveRideStatus,
  clearActiveRide,
  removeFavoriteLocation,
} = userAuthSlice.actions;

// Selectors
export const selectUser = (state: RootState) => state.userAuth.user;
export const selectUserIsAuthenticated = (state: RootState) => state.userAuth.isAuthenticated;
export const selectUserIsLoading = (state: RootState) => state.userAuth.isLoading;
export const selectUserError = (state: RootState) => state.userAuth.error;
export const selectUserToken = (state: RootState) => state.userAuth.token;
export const selectActiveRide = (state: RootState) => state.userAuth.activeRide;
export const selectRideHistory = (state: RootState) => state.userAuth.rideHistory;

export const getUserAuthState = (state: RootState) => state.userAuth;

export type { UserAuthState, RootState };

export default userAuthSlice.reducer;
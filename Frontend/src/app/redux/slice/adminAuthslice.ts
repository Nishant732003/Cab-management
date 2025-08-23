import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';

// Types
export interface AdminStaffUser {
  id: string;
  userName: string;
  email: string;
  userType: 'admin' | 'staff';
}

export interface AdminStaffAuthState {
  user: AdminStaffUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

// Initial state
const initialState: AdminStaffAuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
};

// Async thunks
export const loginUser = createAsyncThunk(
  'adminAuth/login',
  async (credentials: { email: string; password: string }) => {
    // Mock API call - replace with your actual API
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const user: AdminStaffUser = {
      id: '1',
      userName: 'John Doe',
      email: credentials.email,
      userType: credentials.email.includes('admin') ? 'admin' : 'staff'
    };
    
    const token = 'mock-jwt-token-' + Date.now();
    
    // Store in localStorage
    localStorage.setItem('adminToken', token);
    localStorage.setItem('adminUser', JSON.stringify(user));
    
    return { user, token };
  }
);

export const logoutUser = createAsyncThunk(
  'adminAuth/logout',
  async () => {
    // Clear localStorage
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    return null;
  }
);

// Slice
const adminAuthSlice = createSlice({
  name: 'adminAuth',
  initialState,
  reducers: {
    // Sync actions
    setUser: (state, action: PayloadAction<AdminStaffUser>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
      state.error = null;
    },
    clearError: (state) => {
      state.error = null;
    },
    resetAuth: () => initialState,
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Login failed';
        state.isAuthenticated = false;
        state.user = null;
      })
      
      // Logout
      .addCase(logoutUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(logoutUser.fulfilled, () => initialState)
      .addCase(logoutUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Logout failed';
      });
  },
});

// Export actions and reducer
export const { setUser, clearError, resetAuth, setLoading } = adminAuthSlice.actions;
export default adminAuthSlice.reducer;
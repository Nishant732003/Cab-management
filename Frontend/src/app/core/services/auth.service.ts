// auth.service.ts - Updated to handle both email and username
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  email: string;
  username: string; // Add username field
  role: 'admin' | 'driver' | 'user' | 'Customer';
  name: string;
  token?: string;
  userType?: string; 
}

export interface LoginResponse {
  message: string;
  userId: number;
  userType: string;
  token: string;
  success?: boolean; 
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();
  private initialized = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    this.initializeAuthState();
  }

  // Check if input is email
  private isEmail(input: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(input);
  }

  // API login method - updated to handle both email and username
  login(identifier: string, password: string): Observable<LoginResponse> {
    console.log('API login attempt:', identifier);
    
    // Determine if identifier is email or username
    const isEmail = this.isEmail(identifier);
    const loginPayload = isEmail 
      ? { email: identifier, password }
      : { username: identifier, password };
    
    return this.http.post<LoginResponse>(`${environment.apiUrl}api/auth/login`, loginPayload)
      .pipe(
        tap(response => {
          if (response.userId && response.token) {
            const user: User = {
              id: response.userId,
              email: isEmail ? identifier : '', // Set email if it was email
              username: !isEmail ? identifier : '', // Set username if it was username
              role: this.mapUserTypeToRole(response.userType), 
              name: this.extractNameFromIdentifier(identifier), 
              token: response.token,
              userType: response.userType
            };
            this.setUser(user);
          }
        }),
        catchError(error => {
          console.error('Login API error:', error);
          throw error;
        })
      );
  }

  // Extract name from identifier (email or username)
  private extractNameFromIdentifier(identifier: string): string {
    const namePart = identifier.split('@')[0]; // For email, get part before @
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
  }

  logout(): Observable<any> {
    console.log('API logout request');
    
    return this.http.post(`${environment.apiUrl}api/auth/logout`, {}).pipe(
      tap(() => {
        this.clearAuthData();
      }),
      catchError(error => {
        console.error('Logout API error:', error);
        this.clearAuthData();
        throw error;
      })
    );
  }

  // Helper method to clear authentication data
  private clearAuthData(): void {
    this.currentUserSubject.next(null);
    this.removeItem('currentUser');
    this.removeItem('authToken');
  }

  // Map userType from API to role for internal use
  private mapUserTypeToRole(userType: string): User['role'] {
    const typeMap: { [key: string]: User['role'] } = {
      'Customer': 'user',
      'Admin': 'admin',
      'Driver': 'driver',
      'User': 'user'
    };
    
    return typeMap[userType] || 'user'; 
  }

  isLoggedIn(): boolean {
    if (!this.initialized) {
      this.initializeAuthState();
    }
    
    const user = this.currentUserSubject.value;
    const isLoggedIn = user !== null && user.token !== undefined;
    console.log('isLoggedIn check:', isLoggedIn);
    return isLoggedIn;
  }

  getCurrentUser(): User | null {
    if (!this.initialized) {
      this.initializeAuthState();
    }
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    const user = this.getCurrentUser();
    return user?.token || null;
  }

  hasRole(role: string): boolean {
    const currentUser = this.getCurrentUser();
    const hasRole = currentUser?.role === role;
    console.log(`hasRole(${role}) check:`, hasRole);
    return hasRole;
  }

  // Check userType directly from API response
  hasUserType(userType: string): boolean {
    const currentUser = this.getCurrentUser();
    const hasUserType = currentUser?.userType === userType;
    console.log(`hasUserType(${userType}) check:`, hasUserType);
    return hasUserType;
  }

  private setUser(user: User): void {
    console.log('Setting user:', user);
    this.currentUserSubject.next(user);
    this.setItem('currentUser', JSON.stringify(user));
    
    if (user.token) {
      this.setItem('authToken', user.token);
    }
  }

  private initializeAuthState(): void {
    if (this.initialized) return;
    
    console.log('Initializing auth state');
    
    if (isPlatformBrowser(this.platformId)) {
      const userJson = this.getItem('currentUser');
      
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          this.currentUserSubject.next(user);
          console.log('Restored user from storage:', user);
        } catch (e) {
          console.error('Error parsing user data', e);
          this.clearAuthData();
        }
      }
    }
    
    this.initialized = true;
  }

  private setItem(key: string, value: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(key, value);
    }
  }

  private getItem(key: string): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(key);
    }
    return null;
  }

  private removeItem(key: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(key);
    }
  }
}
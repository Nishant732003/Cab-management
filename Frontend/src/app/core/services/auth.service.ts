// auth.service.ts - Final Version with Registration Methods
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  email: string;
  username: string;
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

// Interface for the registration response from the backend
export interface RegistrationResponse {
  message: string;
  userId: number;
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

  /**
   * Workflow: Registers a new user (customer) by calling the backend API.
   * Sends a POST request to the '/api/auth/register/customer' endpoint.
   */
  registerUser(userData: any): Observable<RegistrationResponse> {
    console.log('API call to register new user:', userData);
    return this.http.post<RegistrationResponse>(`${environment.apiUrl}/auth/register/customer`, userData)
      .pipe(
        tap(response => console.log('User registration API successful:', response)),
        catchError(error => {
          console.error('User registration API error:', error);
          throw error;
        })
      );
  }

  /**
   * Workflow: Registers a new driver by calling the backend API.
   * Sends a POST request to the '/api/auth/register/driver' endpoint.
   */
  registerDriver(driverData: any): Observable<RegistrationResponse> {
    console.log('API call to register new driver:', driverData);
    return this.http.post<RegistrationResponse>(`${environment.apiUrl}/auth/register/driver`, driverData)
      .pipe(
        tap(response => console.log('Driver registration API successful:', response)),
        catchError(error => {
          console.error('Driver registration API error:', error);
          throw error;
        })
      );
  }


  // --- All your other existing methods remain below ---

  private isEmail(input: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(input);
  }

  login(identifier: string, password: string): Observable<LoginResponse> {
    console.log('API login attempt:', identifier);
    const isEmail = this.isEmail(identifier);
    const loginPayload = isEmail
      ? { email: identifier, password }
      : { username: identifier, password };

    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, loginPayload)
      .pipe(
        tap(response => {
          if (response.userId && response.token) {
            const user: User = {
              id: response.userId,
              email: isEmail ? identifier : '',
              username: !isEmail ? identifier : '',
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

  private extractNameFromIdentifier(identifier: string): string {
    const namePart = identifier.split('@')[0];
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
  }

  logout(): Observable<any> {
    console.log('API logout request');
    return this.http.post(`${environment.apiUrl}/auth/logout`, {}).pipe(
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

  private clearAuthData(): void {
    this.currentUserSubject.next(null);
    this.removeItem('currentUser');
    this.removeItem('authToken');
  }

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
    return user !== null && user.token !== undefined;
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
    return currentUser?.role === role;
  }

  hasUserType(userType: string): boolean {
    const currentUser = this.getCurrentUser();
    return currentUser?.userType === userType;
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
    if (isPlatformBrowser(this.platformId)) {
      const userJson = this.getItem('currentUser');
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          this.currentUserSubject.next(user);
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
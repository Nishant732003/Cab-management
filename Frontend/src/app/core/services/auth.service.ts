// auth.service.ts - Corrected API Endpoint Path
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

  // API login method - updated to handle both email and username
  login(identifier: string, password: string): Observable<LoginResponse> {
    console.log('API login attempt:', identifier);
    const isEmail = this.isEmail(identifier);
    const loginPayload = isEmail
      ? { email: identifier, password }
      : { username: identifier, password };
    
    //
    // THIS IS THE CORRECTED LINE:
    // The URL must match the backend's @RequestMapping and SecurityConfig
    //
    const loginUrl = `${environment.apiUrl}/api/auth/login`;
    console.log('Attempting to POST to:', loginUrl);

    return this.http.post<LoginResponse>(loginUrl, loginPayload)
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

  // --- The rest of your file is correct and remains the same ---
  
  registerUser(userData: any): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${environment.apiUrl}/api/auth/register/customer`, userData);
  }

  registerDriver(driverData: any): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${environment.apiUrl}/api/auth/register/driver`, driverData);
  }

  private isEmail(input: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(input);
  }

  private extractNameFromIdentifier(identifier: string): string {
    const namePart = identifier.split('@')[0];
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
  }

  logout(): Observable<any> {
    // This URL needs to be correct too
    return this.http.post(`${environment.apiUrl}/api/auth/logout`, {}).pipe(
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
      'Customer': 'user', 'Admin': 'admin', 'Driver': 'driver', 'User': 'user'
    };
    return typeMap[userType] || 'user';
  }

  isLoggedIn(): boolean {
    if (!this.initialized) this.initializeAuthState();
    const user = this.currentUserSubject.value;
    return user !== null && user.token !== undefined;
  }

  getCurrentUser(): User | null {
    if (!this.initialized) this.initializeAuthState();
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return this.getCurrentUser()?.token || null;
  }

  hasRole(role: string): boolean {
    return this.getCurrentUser()?.role === role;
  }

  hasUserType(userType: string): boolean {
    return this.getCurrentUser()?.userType === userType;
  }

  private setUser(user: User): void {
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
          this.currentUserSubject.next(JSON.parse(userJson));
        } catch (e) {
          this.clearAuthData();
        }
      }
    }
    this.initialized = true;
  }

  private setItem(key: string, value: string): void {
    if (isPlatformBrowser(this.platformId)) localStorage.setItem(key, value);
  }

  private getItem(key: string): string | null {
    if (isPlatformBrowser(this.platformId)) return localStorage.getItem(key);
    return null;
  }

  private removeItem(key: string): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem(key);
  }
}

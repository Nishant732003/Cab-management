// auth.service.ts - Fixed version
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';

export interface User {
  id: number;
  email: string;
  role: 'admin' | 'driver' | 'user';
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Track current user state
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();
  private initialized = false;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeAuthState();
  }

  // Mock login - replace with real API call later
  login(email: string, password: string): boolean {
    console.log('Login attempt:', email);
    
    // Simple mock authentication
    if (email === 'admin@cab.com' && password === 'admin123') {
      const user: User = {
        id: 1,
        email: 'admin@cab.com',
        role: 'admin',
        name: 'Admin User'
      };
      this.setUser(user);
      return true;
    }
    
    if (email === 'driver@cab.com' && password === 'driver123') {
      const user: User = {
        id: 2,
        email: 'driver@cab.com',
        role: 'driver', 
        name: 'Driver User'
      };
      this.setUser(user);
      return true;
    }
    
    if (email === 'user@cab.com' && password === 'user123') {
      const user: User = {
        id: 3,
        email: 'user@cab.com',
        role: 'user',
        name: 'Regular User'
      };
      this.setUser(user);
      return true;
    }
    
    return false;
  }

  logout(): void {
    console.log('Logging out user');
    this.currentUserSubject.next(null);
    this.removeItem('currentUser');
  }

  isLoggedIn(): boolean {
    // Ensure we've initialized the state first
    if (!this.initialized) {
      this.initializeAuthState();
    }
    
    const isLoggedIn = this.currentUserSubject.value !== null;
    console.log('isLoggedIn check:', isLoggedIn, 'Current user:', this.currentUserSubject.value);
    return isLoggedIn;
  }

  getCurrentUser(): User | null {
    if (!this.initialized) {
      this.initializeAuthState();
    }
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const currentUser = this.getCurrentUser();
    const hasRole = currentUser?.role === role;
    console.log(`hasRole(${role}) check:`, hasRole, 'Current user role:', currentUser?.role);
    return hasRole;
  }

  private setUser(user: User): void {
    console.log('Setting user:', user);
    this.currentUserSubject.next(user);
    this.setItem('currentUser', JSON.stringify(user));
  }

  private initializeAuthState(): void {
    if (this.initialized) return;
    
    console.log('Initializing auth state, platform browser:', isPlatformBrowser(this.platformId));
    
    // Only access localStorage in the browser
    if (isPlatformBrowser(this.platformId)) {
      const userJson = this.getItem('currentUser');
      console.log('Stored user data:', userJson);
      
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          this.currentUserSubject.next(user);
          console.log('Restored user from storage:', user);
        } catch (e) {
          console.error('Error parsing user data', e);
          this.removeItem('currentUser');
        }
      }
    }
    
    this.initialized = true;
  }

  // Helper methods to safely handle localStorage
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
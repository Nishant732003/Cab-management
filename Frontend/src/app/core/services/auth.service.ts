// auth.service.ts
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from '../../../../node_modules/rxjs/dist/types';

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

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeAuthState();
  }

  // Mock login - replace with real API call later
  login(email: string, password: string): boolean {
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
    this.currentUserSubject.next(null);
    this.removeItem('currentUser');
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    return this.currentUserSubject.value?.role === role;
  }

  private setUser(user: User): void {
    this.currentUserSubject.next(user);
    this.setItem('currentUser', JSON.stringify(user));
  }

  private initializeAuthState(): void {
    // Only access localStorage in the browser
    if (isPlatformBrowser(this.platformId)) {
      const userJson = this.getItem('currentUser');
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          this.currentUserSubject.next(user);
        } catch (e) {
          console.error('Error parsing user data', e);
          this.removeItem('currentUser');
        }
      }
    }
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
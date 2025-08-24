import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Driver,Trip } from '../../models/driver/driver.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  
  private apiUrl = environment.apiUrl || 'http://localhost:3000/api';
  private currentDriverSubject = new BehaviorSubject<Driver | null>(null);
  public currentDriver$ = this.currentDriverSubject.asObservable();

  // Mock data for development
  private mockDriver: Driver = {
    id: 'DRV001',
    name: 'Rajesh Kumar',
    email: 'rajesh@example.com',
    phone: '+91-9876543210',
    avatar: 'https://via.placeholder.com/150/0066CC/FFFFFF?text=RK',
    licenseNumber: 'DL1420110012345',
    rating: 4.7,
    totalTrips: 1250,
    totalEarnings: 125000,
    joinDate: new Date('2022-06-15'),
    isOnline: true,
    vehicle: {
      id: 'VEH001',
      make: 'Maruti Suzuki',
      model: 'Swift Dzire',
      year: 2020,
      color: 'White',
      plateNumber: 'MH 04 AB 1234',
      type: 'sedan' as any,
      fuelType: 'Petrol',
      currentMileage: 45000,
      lastService: new Date('2024-07-15'),
      nextService: new Date('2024-10-15'),
      insurance: {
        provider: 'ICICI Lombard',
        policyNumber: 'POL123456789',
        expiryDate: new Date('2025-06-30'),
        isActive: true
      }
    },
    location: {
      latitude: 19.0760,
      longitude: 72.8777,
      address: 'Andheri West, Mumbai',
      city: 'Mumbai',
      state: 'Maharashtra'
    }
  };

  constructor(private http: HttpClient) {
    // Initialize with mock data for development
    this.currentDriverSubject.next(this.mockDriver);
  }

  getCurrentDriver(): Observable<Driver> {
    // In production, this would make an HTTP call
    // return this.http.get<Driver>(`${this.apiUrl}/driver/profile`);
    
    // For development, return mock data
    return of(this.mockDriver);
  }

  updateDriverProfile(driver: Partial<Driver>): Observable<boolean> {
    // return this.http.put<boolean>(`${this.apiUrl}/driver/profile`, driver);
    
    // Mock implementation
    Object.assign(this.mockDriver, driver);
    this.currentDriverSubject.next(this.mockDriver);
    return of(true);
  }

  updateOnlineStatus(isOnline: boolean): Observable<boolean> {
    // return this.http.patch<boolean>(`${this.apiUrl}/driver/status`, { isOnline });
    
    // Mock implementation
    this.mockDriver.isOnline = isOnline;
    this.currentDriverSubject.next(this.mockDriver);
    return of(true);
  }

  getDashboardStats(): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/driver/dashboard-stats`);
    
    // Mock implementation
    return of({
      pendingTrips: 2,
      unreadNotifications: 5,
      vehicleAlerts: true,
      todayEarnings: 2450
    });
  }

  getTodayStats(): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/driver/today-stats`);
    
    // Mock implementation
    return of({
      earnings: 2450,
      trips: 12,
      distance: 180,
      earningsChange: 15.5,
      tripsChange: 8.2
    });
  }

  getCurrentTrip(): Observable<Trip | null> {
    // return this.http.get<Trip>(`${this.apiUrl}/driver/current-trip`);
    
    // Mock implementation - no current trip
    return of(null);
  }

  getVehicleStatus(): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/driver/vehicle-status`);
    
    // Mock implementation
    return of({
      fuelLevel: 68,
      isServiceDue: false,
      alerts: [
        { type: 'warning', message: 'Insurance expires in 30 days' }
      ]
    });
  }

  getNotifications(): Observable<any[]> {
    // return this.http.get<any[]>(`${this.apiUrl}/driver/notifications`);
    
    // Mock implementation
    return of([
      {
        id: '1',
        title: 'Trip Completed',
        message: 'You have completed a trip to Airport Terminal 2',
        type: 'trip',
        timestamp: new Date(Date.now() - 1000 * 60 * 30), // 30 minutes ago
        isRead: false
      },
      {
        id: '2',
        title: 'Weekly Earnings',
        message: 'You earned ₹15,240 this week',
        type: 'earning',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2 hours ago
        isRead: false
      },
      {
        id: '3',
        title: 'Vehicle Service Reminder',
        message: 'Your vehicle service is due in 15 days',
        type: 'alert',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1 day ago
        isRead: true
      }
    ]);
  }

  markNotificationAsRead(notificationId: string): Observable<boolean> {
    // return this.http.patch<boolean>(`${this.apiUrl}/driver/notifications/${notificationId}/read`, {});
    
    // Mock implementation
    return of(true);
  }

  clearAllNotifications(): Observable<boolean> {
    // return this.http.delete<boolean>(`${this.apiUrl}/driver/notifications`);
    
    // Mock implementation
    return of(true);
  }

  updateLocation(location: { latitude: number; longitude: number }): Observable<boolean> {
    // return this.http.patch<boolean>(`${this.apiUrl}/driver/location`, location);
    
    // Mock implementation
    this.mockDriver.location.latitude = location.latitude;
    this.mockDriver.location.longitude = location.longitude;
    return of(true);
  }

  logout(): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/logout`, {});
    
    // Mock implementation
    this.currentDriverSubject.next(null);
    localStorage.removeItem('driver_token');
    return of(true);
  }

  // Emergency and support functions
  sendSOS(location: { latitude: number; longitude: number }): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/sos`, { location });
    
    // Mock implementation
    console.log('SOS sent to emergency services');
    return of(true);
  }

  reportIssue(issue: { type: string; description: string; severity: string }): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/report-issue`, issue);
    
    // Mock implementation
    console.log('Issue reported:', issue);
    return of(true);
  }
}
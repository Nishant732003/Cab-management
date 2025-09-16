import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// import { Trip, TripStatus, PaymentMethod } from '../models/driver.model';
import { Trip,TripStatus,PaymentMethod } from '../../models/driver/driver.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TripService {
  
  private apiUrl = environment.apiUrl || 'http://localhost:3000/api';

  // Mock trip data for development
  private mockTrips: Trip[] = [
    {
      id: 'TRP001',
      driverId: 'DRV001',
      passengerId: 'PASS001',
      passengerName: 'Amit Sharma',
      passengerPhone: '+91-9876543210',
      pickupLocation: {
        latitude: 19.0760,
        longitude: 72.8777,
        address: 'Andheri West Railway Station',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      dropLocation: {
        latitude: 19.0896,
        longitude: 72.8656,
        address: 'Mumbai Domestic Airport Terminal 1',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      startTime: new Date(Date.now() - 1000 * 60 * 45), // 45 minutes ago
      endTime: new Date(Date.now() - 1000 * 60 * 15), // 15 minutes ago
      distance: 12.5,
      duration: 30,
      fare: 285,
      tip: 25,
      status: TripStatus.COMPLETED,
      paymentMethod: PaymentMethod.UPI,
      rating: 5
    },
    {
      id: 'TRP002',
      driverId: 'DRV001',
      passengerId: 'PASS002',
      passengerName: 'Priya Patel',
      passengerPhone: '+91-9876543211',
      pickupLocation: {
        latitude: 19.0596,
        longitude: 72.8295,
        address: 'Bandra Kurla Complex',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      dropLocation: {
        latitude: 19.0176,
        longitude: 72.8562,
        address: 'Lower Parel',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      startTime: new Date(Date.now() - 1000 * 60 * 120), // 2 hours ago
      endTime: new Date(Date.now() - 1000 * 60 * 95), // 1 hour 35 minutes ago
      distance: 8.2,
      duration: 25,
      fare: 195,
      tip: 15,
      status: TripStatus.COMPLETED,
      paymentMethod: PaymentMethod.CARD,
      rating: 4
    },
    {
      id: 'TRP003',
      driverId: 'DRV001',
      passengerId: 'PASS003',
      passengerName: 'Rohit Singh',
      passengerPhone: '+91-9876543212',
      pickupLocation: {
        latitude: 19.0728,
        longitude: 72.8826,
        address: 'Linking Road, Bandra',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      dropLocation: {
        latitude: 19.0330,
        longitude: 72.8697,
        address: 'Worli Sea Face',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      startTime: new Date(Date.now() - 1000 * 60 * 180), // 3 hours ago
      endTime: new Date(Date.now() - 1000 * 60 * 160), // 2 hours 40 minutes ago
      distance: 6.8,
      duration: 20,
      fare: 165,
      tip: 0,
      status: TripStatus.COMPLETED,
      paymentMethod: PaymentMethod.CASH,
      rating: 4
    },
    {
      id: 'TRP004',
      driverId: 'DRV001',
      passengerId: 'PASS004',
      passengerName: 'Sneha Desai',
      passengerPhone: '+91-9876543213',
      pickupLocation: {
        latitude: 19.0896,
        longitude: 72.8656,
        address: 'Mumbai Domestic Airport Terminal 1',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      dropLocation: {
        latitude: 19.0760,
        longitude: 72.8777,
        address: 'Andheri West',
        city: 'Mumbai',
        state: 'Maharashtra'
      },
      startTime: new Date(Date.now() - 1000 * 60 * 60 * 4), // 4 hours ago
      endTime: new Date(Date.now() - 1000 * 60 * 60 * 4 + 1000 * 60 * 15), // 3 hours 45 minutes ago
      distance: 4.2,
      duration: 15,
      fare: 125,
      tip: 10,
      status: TripStatus.COMPLETED,
      paymentMethod: PaymentMethod.WALLET,
      rating: 5
    }
  ];

  constructor(private http: HttpClient) {}

  getRecentTrips(limit: number = 10): Observable<Trip[]> {
    // In production:
    // return this.http.get<Trip[]>(`${this.apiUrl}/driver/trips/recent?limit=${limit}`);
    
    // Mock implementation
    return of(this.mockTrips.slice(0, limit));
  }

  getAllTrips(page: number = 1, pageSize: number = 10, filters?: any): Observable<{ trips: Trip[], total: number }> {
    // In production:
    // return this.http.get<{trips: Trip[], total: number}>(`${this.apiUrl}/driver/trips`, {
    //   params: { page: page.toString(), pageSize: pageSize.toString(), ...filters }
    // });
    
    // Mock implementation
    let filteredTrips = [...this.mockTrips];
    
    // Apply filters if provided
    if (filters) {
      if (filters.status) {
        filteredTrips = filteredTrips.filter(trip => trip.status === filters.status);
      }
      if (filters.dateFrom) {
        filteredTrips = filteredTrips.filter(trip => new Date(trip.startTime) >= new Date(filters.dateFrom));
      }
      if (filters.dateTo) {
        filteredTrips = filteredTrips.filter(trip => new Date(trip.startTime) <= new Date(filters.dateTo));
      }
    }
    
    // Pagination
    const startIndex = (page - 1) * pageSize;
    const paginatedTrips = filteredTrips.slice(startIndex, startIndex + pageSize);
    
    return of({
      trips: paginatedTrips,
      total: filteredTrips.length
    });
  }

  getTripById(tripId: string): Observable<Trip | null> {
    // return this.http.get<Trip>(`${this.apiUrl}/driver/trips/${tripId}`);
    
    // Mock implementation
    const trip = this.mockTrips.find(t => t.id === tripId);
    return of(trip || null);
  }

  acceptTrip(tripId: string): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/trips/${tripId}/accept`, {});
    
    // Mock implementation
    const trip = this.mockTrips.find(t => t.id === tripId);
    if (trip) {
      trip.status = TripStatus.ACCEPTED;
      return of(true);
    }
    return of(false);
  }

  startTrip(tripId: string): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/trips/${tripId}/start`, {});
    
    // Mock implementation
    const trip = this.mockTrips.find(t => t.id === tripId);
    if (trip) {
      trip.status = TripStatus.IN_PROGRESS;
      trip.startTime = new Date();
      return of(true);
    }
    return of(false);
  }

  completeTrip(tripId: string, completionData: { endTime: Date, actualFare: number, otp?: string }): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/trips/${tripId}/complete`, completionData);
    
    // Mock implementation
    const trip = this.mockTrips.find(t => t.id === tripId);
    if (trip) {
      trip.status = TripStatus.COMPLETED;
      trip.endTime = completionData.endTime;
      trip.fare = completionData.actualFare;
      return of(true);
    }
    return of(false);
  }

  cancelTrip(tripId: string, reason: string): Observable<boolean> {
    // return this.http.post<boolean>(`${this.apiUrl}/driver/trips/${tripId}/cancel`, { reason });
    
    // Mock implementation
    const trip = this.mockTrips.find(t => t.id === tripId);
    if (trip) {
      trip.status = TripStatus.CANCELLED;
      return of(true);
    }
    return of(false);
  }

  updateTripLocation(tripId: string, location: { latitude: number, longitude: number }): Observable<boolean> {
    // return this.http.patch<boolean>(`${this.apiUrl}/driver/trips/${tripId}/location`, location);
    
    // Mock implementation
    return of(true);
  }

  getTripStats(period: 'today' | 'week' | 'month' | 'year' = 'today'): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/driver/trips/stats?period=${period}`);
    
    // Mock implementation
    const now = new Date();
    let startDate: Date;
    
    switch (period) {
      case 'today':
        startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        break;
      case 'week':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case 'month':
        startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        break;
      case 'year':
        startDate = new Date(now.getFullYear(), 0, 1);
        break;
      default:
        startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    }
    
    const filteredTrips = this.mockTrips.filter(trip => 
      new Date(trip.startTime) >= startDate && trip.status === TripStatus.COMPLETED
    );
    
    return of({
      totalTrips: filteredTrips.length,
      totalDistance: filteredTrips.reduce((sum, trip) => sum + trip.distance, 0),
      totalEarnings: filteredTrips.reduce((sum, trip) => sum + trip.fare + trip.tip, 0),
      averageRating: filteredTrips.length > 0 ? 
        filteredTrips.reduce((sum, trip) => sum + trip.rating, 0) / filteredTrips.length : 0,
      averageTripTime: filteredTrips.length > 0 ?
        filteredTrips.reduce((sum, trip) => sum + trip.duration, 0) / filteredTrips.length : 0
    });
  }

  // Real-time trip requests (would typically use WebSocket)
  getPendingTripRequests(): Observable<Trip[]> {
    // In production, this would be a WebSocket connection or SSE
    // return this.webSocketService.connect('/driver/trip-requests');
    
    // Mock implementation - no pending requests
    return of([]);
  }

  // Navigation and directions
  getDirections(from: { lat: number, lng: number }, to: { lat: number, lng: number }): Observable<any> {
    // return this.http.get<any>(`${this.apiUrl}/directions?from=${from.lat},${from.lng}&to=${to.lat},${to.lng}`);
    
    // Mock implementation
    return of({
      distance: '12.5 km',
      duration: '25 mins',
      route: [
        { lat: from.lat, lng: from.lng },
        { lat: to.lat, lng: to.lng }
      ]
    });
  }
}
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
// Request/Response interfaces
export interface BookRideRequest {
  customerId: number;
  fromLocation: string;
  toLocation: string;
  distanceInKm: number;
  carType: string;
  scheduledTime?: string | null;
}

export interface Customer {
  id: number;
  username: string;
  address: string | null;
  mobileNumber: string | null;
  email: string;
  emailVerified: boolean;
}

export interface Driver {
  id: number;
  username: string;
  address: string | null;
  mobileNumber: string | null;
  email: string;
  emailVerified: boolean;
  licenceNo: string;
  rating: number;
  verified: boolean;
  isAvailable: boolean;
  totalRatings: number;
  profilePhotoUrl: string | null;
}

export interface Cab {
  cabId: number;
  carType: string;
  perKmRate: number;
  numberPlate: string;
  imageUrl: string | null;
  isAvailable: boolean;
}

export interface BookRideResponse {
  tripBookingId: number;
  customer: Customer;
  driver: Driver;
  cab: Cab;
  fromLocation: string;
  toLocation: string;
  fromDateTime: string;
  toDateTime: string | null;
  status: string;
  distanceInKm: number;
  bill: number;
  customerRating: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private apiUrl =  environment.apiUrl; // Replace with your actual API URL

  constructor(private http: HttpClient) {}

  // Get authorization headers with token from localStorage
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken'); 
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }


  bookRide(rideRequest: BookRideRequest): Observable<BookRideResponse> {
    return this.http.post<BookRideResponse>(
      `${this.apiUrl}/trips/book`, 
      rideRequest,
      { headers: this.getAuthHeaders() }
    );
  }


  // Get available drivers (if you have this endpoint)
  getAvailableDrivers(fromLocation: string, toLocation: string): Observable<Driver[]> {
    const params = {
      fromLocation,
      toLocation
    };
    
    return this.http.get<Driver[]>(
      `${this.apiUrl}/drivers/available`,
      { 
        headers: this.getAuthHeaders(),
        params
      }
    );
  }

  // Get available cabs (if you have this endpoint)
//   getAvailableCabs(carType?: string): Observable<Cab[]> {
//     const params = carType ? { carType } : {};
    
//     return this.http.get<Cab[]>(
//       `${this.apiUrl}/cabs/available`,
//       { 
//         headers: this.getAuthHeaders(),
//         params
//       }
//     );
//   }

  // Get ride details by ID
  getRideDetails(tripBookingId: number): Observable<BookRideResponse> {
    return this.http.get<BookRideResponse>(
      `${this.apiUrl}/rides/${tripBookingId}`,
      { headers: this.getAuthHeaders() }
    );
  }
   getCustomerTrips(customerId: number): Observable<BookRideResponse[]> {
    return this.http.get<BookRideResponse[]>(
      `${this.apiUrl}/trips/customer/${customerId}`,
      { headers: this.getAuthHeaders() }
    );
  }


  // Cancel a ride
  cancelRide(tripBookingId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/rides/${tripBookingId}/cancel`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  // Rate a ride
  rateRide(tripBookingId: number, rating: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/rides/${tripBookingId}/rate`,
      { customerRating: rating },
      { headers: this.getAuthHeaders() }
    );
  }

  // Helper method to get fallback image
  getDriverImage(driver: Driver): string {
    return driver.profilePhotoUrl || 'assets/images/driver.avif';
  }

  // Helper method to get fallback cab image
  getCabImage(cab: Cab): string {
    return cab.imageUrl || 'assets/images/default-car.jpg';
  }

  // Calculate estimated fare
  calculateFare(distanceInKm: number, perKmRate: number, baseFare: number = 50): number {
    return baseFare + (distanceInKm * perKmRate);
  }
}
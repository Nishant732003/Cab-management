import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Customer {
  userId: number;
  username: string;
  name: string;
  email: string;
  mobileNumber: string;
}

// FIX: Add a new interface for Driver data to match the DTO
export interface Driver {
  userId: number;
  username: string;
  name: string;
  email: string;
  mobileNumber: string;
  rating: number;
  licenceNo: string;
  verified: boolean;
  vehicle?: string; // Vehicle might come from a different source, so optional
}
// NEW: INTERFACE FOR ADMIN
export interface Admin {
  userId: number;
  username: string;
  name: string;
  email: string;
  mobileNumber: string;
  verified: boolean;
}
// NEW: INTERFACE FOR TRIP
export interface TripBooking {
  tripBookingId: number;
  customer: Customer;
  driver: Driver;
  fromLocation: string;
  toLocation: string;
  fromDateTime: string;
  toDateTime: string | null;
  status: string;
  distanceInKm: number;
  bill: number;
  customerRating: number | null;
}

  // Add other relevant trip properties


@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/api/admin`;

  constructor(private http: HttpClient) { }

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/customers`);
  }

  // FIX: Add the new method to get all drivers
  getAllDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.apiUrl}/drivers`);
  }
  // --- FIX: ADD METHODS FOR DRIVER VERIFICATION ---

  /**
   * Fetches a list of all drivers who are not yet verified.
   * This corresponds to GET /api/admin/unverified/drivers
   * @returns An Observable array of Driver objects.
   */
  getUnverifiedDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.apiUrl}/unverified/drivers`);
  }

  /**
   * Sends a request to verify a specific driver.
   * This corresponds to POST /api/admin/verify/drivers/{driverId}
   * @param driverId The unique ID of the driver to verify.
   * @returns An Observable with the response from the server.
   */
  verifyDriver(driverId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify/drivers/${driverId}`, {});
 
  }
   // New methods to add:

  /**
   * Fetches all unverified admin accounts.
   */
  getUnverifiedAdmins(): Observable<Admin[]> {
    return this.http.get<Admin[]>(`${this.apiUrl}/unverified/admins`);
  }

  /**
   * Verifies an admin by their ID.
   * @param adminId The ID of the admin to verify.
   */
  verifyAdmin(adminId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify/admins/${adminId}`, {});
  }

  /**
   * Gets all trips for a specific driver.
   * @param driverId The ID of the driver.
   */
  getTripsByDriver(driverId: number): Observable<TripBooking[]> {
    return this.http.get<TripBooking[]>(`${this.apiUrl}/trips/driver/${driverId}`);
  }

  /**
   * Gets all trips for a specific date.
   * @param date The date in YYYY-MM-DD format.
   */
  getTripsByDate(date: string): Observable<TripBooking[]> {
    return this.http.get<TripBooking[]>(`${this.apiUrl}/trips/date/${date}`);
  }
}





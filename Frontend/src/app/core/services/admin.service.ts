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

/**
 * FIX: The 'export' keyword was missing.
 * This interface is for a user (Customer or Driver) within a trip record.
 */
export interface TripUser {
  id: number;
  username: string;
}
// NEW: INTERFACE FOR TRIP
export interface TripBooking {
  tripBookingId: number;
  customer: TripUser; // FIX: Uses an interface that has an 'id'
  driver: TripUser;   // FIX: Uses an interface that has an 'id'
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
// --- FIX: Correctly typed the return value and the http.get call ---
 /**
   * FIX: The return type is now explicitly Observable<TripBooking[]>.
   * This tells TypeScript what kind of data to expect from the API,
   * fixing the "Type 'unknown' is not assignable" error.
   */
  getTripsByDriver(driverId: number): Observable<TripBooking[]> {
    return this.http.get<TripBooking[]>(`${this.apiUrl}/trips/driver/${driverId}`);
  }

  /**
   * FIX: The return type is also explicitly Observable<TripBooking[]>.
   */
  getTripsByDate(date: string): Observable<TripBooking[]> {
    return this.http.get<TripBooking[]>(`${this.apiUrl}/trips/date/${date}`);
  }
}





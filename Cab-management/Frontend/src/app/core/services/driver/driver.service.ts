import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CabUpdateRequest {
  numberPlate: string;
  carType: string;
  perKmRate: number;
}
 // In src/app/core/services/driver/driver.service.ts
export interface Trip {
distanceinKm: number;
  tripBookingId: number;
  fromLocation: string;
  toLocation: string;
  fromDateTime: string;
  toDateTime: string | null;
  status: string;
  bill: number;
  customerRating: number | null;
  carType: string;
  customerFirstName: string;
  customerLastName: string;
  driverFirstName: string;
  driverLastName: string;
  canRate?: boolean;
}
export interface Cab {
  cabId: number; // Changed from id to cabId
  numberPlate: string;
  carType: string;
  perKmRate: number;
  imageUrl?: string | null;
  isAvailable?: boolean;
}

@Injectable({ providedIn: 'root' })
export class DriverService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}


  updateCabForDriver(driverId: number, payload: CabUpdateRequest): Observable<Cab> {
    return this.http.put<Cab>(`${this.baseUrl}/api/cabs/${driverId}/update`, payload);
  }

  uploadCabImage(cabId: number, file: File): Observable<HttpEvent<Cab>> {
    const form = new FormData();
    form.append('file', file);
    
    const req = new HttpRequest(
      'PUT', 
      `${this.baseUrl}/api/cabs/${cabId}/image`, 
      form, 
      {
        reportProgress: true
      }
    );
    return this.http.request<Cab>(req);
  }

  deleteCabImage(cabId: number): Observable<Cab> {
    return this.http.delete<Cab>(`${this.baseUrl}/api/cabs/${cabId}/image`); 
  }
  getTripsForDriver(driverId: number): Observable<Trip[]> {
  return this.http.get<Trip[]>(`${this.baseUrl}/api/trips/driver/${driverId}`);
}
 completeTripsForDriver(driverId: number): Observable<Trip[]> {
    return this.http.put<Trip[]>(`${this.baseUrl}/api/trips/${driverId}/complete`, {});
  }
// ----Correctly use .put to match the backend controller---- added by me
   updateTripStatus(tripId: number, status: string): Observable<Trip> {
    return this.http.put<Trip>(`${this.baseUrl}/api/trips/${tripId}/status?status=${status}`, {});
  }

  startTrip(tripId: number): Observable<Trip> {
    return this.updateTripStatus(tripId, 'IN_PROGRESS');
  }
  /**
   * This is the corrected method.
   * It now calls the dedicated 'complete' endpoint instead of the generic 'status' endpoint.
   -------------added by me */

  completeTrip(tripId: number): Observable<Trip> {
    return this.http.put<Trip>(`${this.baseUrl}/api/trips/${tripId}/complete`, {});
  }

  cancelTrip(tripId: number): Observable<Trip> {
    return this.updateTripStatus(tripId, 'CANCELLED');
  }

   getDriverProfile(username: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/profiles/${username}`);
  }
}


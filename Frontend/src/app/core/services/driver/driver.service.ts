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

  // PUT /api/cabs/{driverId}/update
  updateCabForDriver(driverId: number, payload: CabUpdateRequest): Observable<Cab> {
    return this.http.put<Cab>(`${this.baseUrl}/api/cabs/${driverId}/update`, payload);
  }

  // PUT /api/cabs/{cabId}/image with FormData field name "file"
  uploadCabImage(cabId: number, file: File): Observable<HttpEvent<Cab>> {
    const form = new FormData();
    form.append('file', file);
    
    const req = new HttpRequest(
      'PUT', 
      `${this.baseUrl}/api/cabs/${cabId}/image`, // Fixed endpoint URL
      form, 
      {
        reportProgress: true
      }
    );
    return this.http.request<Cab>(req);
  }

  // DELETE /api/cabs/{cabId}/image
  deleteCabImage(cabId: number): Observable<Cab> {
    return this.http.delete<Cab>(`${this.baseUrl}/api/cabs/${cabId}/image`); // Fixed endpoint URL
  }
  getTripsForDriver(driverId: number): Observable<Trip[]> {
  return this.http.get<Trip[]>(`${this.baseUrl}/api/trips/driver/${driverId}`);
}
}


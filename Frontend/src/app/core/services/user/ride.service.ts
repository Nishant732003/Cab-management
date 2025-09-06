import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

export interface BookRideRequest {
  customerId: number;
  fromLocation: string;
  toLocation: string;
  distanceInKm: number;
  carType: string;
  fromLatitude: number;
  fromLongitude: number;
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
  isAvailable: boolean;
  numberPlate?: string;
  imageUrl?: string | null;
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

@Injectable({ providedIn: 'root' })
export class RideService {
  private apiUrl = environment.apiUrl;   // e.g., https://example.com/api
  private orsApiKey = environment.openRouteServiceApiKey;

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  // Backend trips
  bookRide(rideRequest: BookRideRequest): Observable<BookRideResponse> {
    return this.http.post<BookRideResponse>(
      `${this.apiUrl}/api/trips/book`,
      rideRequest,
      { headers: this.getAuthHeaders() }
    );
  }

  // Backend drivers (optional, kept if needed elsewhere)
  getAvailableDrivers(fromLocation: string, toLocation: string): Observable<Driver[]> {
    const params = new HttpParams().set('fromLocation', fromLocation).set('toLocation', toLocation);
    return this.http.get<Driver[]>(
      `${this.apiUrl}/drivers/available`,
      { headers: this.getAuthHeaders(), params }
    );
  }

  // Backend cabs for vehicle selection cards
  listCabs(): Observable<Cab[]> {
    return this.http.get<Cab[]>(
      `${this.apiUrl}/api/cabs/all`,
      { headers: this.getAuthHeaders() }
    );
  }

  // ORS geocoding (Pelias Search)
// ORS geocoding (Pelias Search)
geocodeAddress(address: string): Observable<{ lat: number, lng: number }> {

const url = `/api/ors/geocode/search?api_key=${this.orsApiKey}&text=${encodeURIComponent(address)}&size=1`;
 
return this.http.get<any>(url).pipe(
 map(res => {
const coords: [number, number] | undefined =
 res?.features?.[0]?.geometry?.coordinates;

 if (Array.isArray(coords) && coords.length >= 2) {
const [lon, lat] = coords;
 return { lat, lng: lon };
}
 throw new Error('Location not found');
 })
 );
}


  // ORS driving distance (km)
// ORS driving distance (km)
getDistance(fromLat: number, fromLng: number, toLat: number, toLng: number): Observable<number> {
  // Update to use the proxy path
  const url = `/api/ors/v2/directions/driving-car?api_key=${this.orsApiKey}&start=${fromLng},${fromLat}&end=${toLng},${toLat}`;

  return this.http.get<any>(url).pipe(
    map(res => {
      const meters: number | undefined = res?.features?.[0]?.properties?.summary?.distance;
      if (typeof meters === 'number') {
        return meters / 1000; // convert to km
      }
      throw new Error('Could not calculate distance');
    })
  );
}

  // Ride details and customer trips (optional)
  getRideDetails(tripBookingId: number): Observable<BookRideResponse> {
    return this.http.get<BookRideResponse>(
      `${this.apiUrl}/rides/${tripBookingId}`,
      { headers: this.getAuthHeaders() }
    );
  }

 getCustomerTrips(customerId: number): Observable<BookRideResponse[]> {
    return this.http.get<BookRideResponse[]>(
      `${this.apiUrl}/api/trips/customer/${customerId}`,
      { headers: this.getAuthHeaders() }
    );
  }
  cancelRide(tripBookingId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/rides/${tripBookingId}/cancel`, {},
      { headers: this.getAuthHeaders() }
    );
  }

  rateRide(tripBookingId: number, rating: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/rides/${tripBookingId}/rate`,
      { customerRating: rating },
      { headers: this.getAuthHeaders() }
    );
  }

  // Helpers
   getDriverImage(driver: Partial<Driver> | null): string {
  if (!driver || !driver.profilePhotoUrl) {
    return 'assets/images/driver.avif';
  }
  return driver.profilePhotoUrl;
}

  getCabImage(cab: Cab): string {
    return cab.imageUrl || 'assets/images/default-car.jpg';
  }

  calculateFare(distanceInKm: number, perKmRate: number, baseFare: number = 50): number {
    return baseFare + (distanceInKm * perKmRate);
  }
}

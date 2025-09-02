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
}


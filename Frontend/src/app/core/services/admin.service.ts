import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Define an interface for the customer data we expect from the API
export interface Customer {
  userId: number;
  username: string;
  name: string;
  email: string;
  mobileNumber: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/api/admin`;

  constructor(private http: HttpClient) { }

  /**
   * Fetches a list of all customers.
   * @returns An Observable array of Customer objects.
   */
  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/customers`);
  }
}
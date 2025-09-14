// src/app/core/services/admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Customer {
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
}

export interface Driver {
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  rating: number;
  licenceNo: string;
  verified: boolean;
  vehicle?: string;
}

export interface Admin {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  verified: boolean;
  profilePhotoUrl?: string | null;
  address?: string;
  emailVerified?: boolean;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  // Profiles
  getAdminProfile(username: string): Observable<Admin> {
    return this.http.get<Admin>(`${this.apiUrl}/api/profiles/${username}`);
  } // GET {{url}}/api/profiles/{{username}} returns profile object as shown [21]

  // Upload photo (no ID in URL; backend returns updated profile with profilePhotoUrl)
  uploadAdminPhoto(file: File): Observable<HttpEvent<Admin>> {
    const form = new FormData();
    form.append('file', file);
    const req = new HttpRequest(
      'POST',
      `${this.apiUrl}/api/admin/upload-photo`,
      form,
      { reportProgress: true }
    );
    return this.http.request<Admin>(req);
  } // Standard Angular FormData + HttpRequest with progress events [4][3]

  // Optional: If backend adds a delete endpoint for admin photo (adjust path if exists)
  deleteAdminPhoto(): Observable<Admin> {
    // Implement only if backend supports deleting the latest profile photo
    return this.http.delete<Admin>(`${this.apiUrl}/api/drivers/upload-photo`);
  } // Placeholder; remove if backend has a different delete route [4]

  // Resolve a relative /api/files/... to absolute and insert /view if missing
  getProfileImageUrl(profilePhotoUrl?: string | null): string | null {
    if (!profilePhotoUrl) return null;
    const needsView = profilePhotoUrl.startsWith('/api/files/') && !profilePhotoUrl.includes('/view/');
    const parts = profilePhotoUrl.split('/');
    if (needsView) parts.splice(3, 0, 'view');
    const corrected = parts.join('/');
    return `${this.apiUrl}${corrected}`;
  } // Matches your VehicleComponent buildImageUrl logic [22][23]

  // Existing admin APIs
  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/api/admin/customers`);
  } // For stats counts [21]

  getAllDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.apiUrl}/api/admin/drivers`);
  } // For stats counts and verified filter [21]

  getUnverifiedDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.apiUrl}/api/admin/unverified/drivers`);
  } // For verification queues [21]

  verifyDriver(driverId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/admin/verify/drivers/${driverId}`, {});
  } // Verify driver [21]

  getUnverifiedAdmins(): Observable<Admin[]> {
    return this.http.get<Admin[]>(`${this.apiUrl}/api/admin/unverified/admins`);
  } // For admin verification flows [21]

  deleteUser(username: string): Observable<string> {
    return this.http.delete(`${this.apiUrl}/api/auth/delete/${username}`, { responseType: 'text' });
  } // Plain text delete confirmation [21]

  verifyAdmin(adminId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/admin/verify/admins/${adminId}`, {});
  } // Verify admin [21]
}

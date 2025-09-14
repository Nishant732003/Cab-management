import { Component, OnInit } from '@angular/core';
import { DriverService, CabUpdateRequest, Cab } from '../../../../core/services/driver/driver.service';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../../../environments/environment';
import { ChangeDetectorRef } from '@angular/core';@Component({
  selector: 'app-vehicle',
  standalone: false,
  templateUrl: './vehicle.component.html',
  styleUrls: ['./vehicle.component.css']
})
export class VehicleComponent implements OnInit {
  driverId: number | null = null;
  cabId: number | null = null;
  imageUrl: string | null = null;

  form: CabUpdateRequest = {
    numberPlate: 'DL1RT-1226',
    carType: 'Mini',
    perKmRate: 25.0
  };

  selectedFile: File | null = null;
  uploadProgress = 0;
  isUploading = false;
  isSaving = false;

  updatedCab: Cab | null = null;
  errorMsg: string | null = null;


constructor(private driverSvc: DriverService, private cdr: ChangeDetectorRef) {}


  ngOnInit() {
    this.getDriverIdFromCurrentUser();
  }

  private getDriverIdFromCurrentUser() {
    try {
      const userStr = localStorage.getItem('currentUser');
      
      if (!userStr) {
        this.errorMsg = 'User not authenticated';
        return;
      }

      const user = JSON.parse(userStr);
      
      if (!user?.id) {
        this.errorMsg = 'Invalid user data';
        return;
      }

      this.driverId = user.id;
    } catch (error) {
      this.errorMsg = 'Failed to parse user data';
    }
  }

   private buildImageUrl(backendPath: string): string {
    // The backend path is missing "/view/", so we insert it
    const parts = backendPath.split('/');
    parts.splice(3, 0, 'view'); // Inserts 'view' at the 3rd index
    const correctedPath = parts.join('/');
    return `${environment.apiUrl}${correctedPath}`;
  }

  onFileSelected(evt: Event) {
    const input = evt.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Get the first file
    } else {
      this.selectedFile = null;
    }
  }

  onSaveCab() {
    this.errorMsg = null;
    this.updatedCab = null;
    this.imageUrl = null; // Reset image URL
    
    if (this.driverId == null) {
      this.errorMsg = 'Unable to determine driver ID';
      return;
    }
    
    this.isSaving = true;
    this.driverSvc.updateCabForDriver(this.driverId, this.form)
      .pipe(finalize(() => this.isSaving = false))
      .subscribe({
        next: (cab: any) => {
          this.updatedCab = cab;
          // Set cabId and imageUrl from the response
          if (cab?.cabId) {
            this.cabId = cab.cabId;
          }
          if (cab?.imageUrl) {
            this.imageUrl =  this.buildImageUrl(cab.imageUrl);
          }
        },
        error: (err: any) => this.errorMsg = err?.error?.message || 'Failed to save cab'
      });
  }

  onUploadImage() {
    this.errorMsg = null;
    
    if (this.cabId == null) {
      this.errorMsg = 'Please save cab details first to get a cab ID';
      return;
    }
    
    if (!this.selectedFile) {
      this.errorMsg = 'Please choose an image file';
      return;
    }
    
    this.isUploading = true;
    this.uploadProgress = 0;

    this.driverSvc.uploadCabImage(this.cabId, this.selectedFile)
      .pipe(finalize(() => this.isUploading = false))
      .subscribe({
        next: (event: any) => {
          // Track progress events
          if (event.type === 1 && event.total) {
            const loaded = event.loaded;
            const total = event.total;
            this.uploadProgress = Math.round((loaded / total) * 100);
          }
          // Handle response body
          if (event.body) {
            this.updatedCab = event.body as Cab;
             if (event.body.imageUrl) {
            const raw = event.body.imageUrl;
            const normalized = this.buildImageUrl(raw);
            // cache-busting to force fresh fetch
            this.imageUrl = `${normalized}${normalized.includes('?') ? '&' : '?'}t=${Date.now()}`;
            this.cdr.detectChanges(); // if using OnPush or preview still lags
          }
          }
        },
        error: (err: any) => this.errorMsg = err?.error?.message || 'Failed to upload image'
      });
  }

  onDeleteImage() {
    this.errorMsg = null;
    
    if (this.cabId == null) {
      this.errorMsg = 'No cab ID available. Please save cab details first.';
      return;
    }
    
    this.driverSvc.deleteCabImage(this.cabId).subscribe({
      next: (cab: any) => {
        this.updatedCab = cab;
        this.imageUrl = null; // Clear the image URL after deletion
      },
      error: (err: any) => this.errorMsg = err?.error?.message || 'Failed to delete image'
    });
  }
}
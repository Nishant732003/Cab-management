import { Component, OnInit } from '@angular/core';
import { RideService, BookRideRequest, BookRideResponse, Cab } from '../../../../core/services/user/ride.service';

interface LocationSuggestion {
  name: string;
  address: string;
  lat: number;
  lng: number;
}

@Component({
  selector: 'app-book-ride',
  standalone: false,
  templateUrl: './bookRide.component.html',
  styleUrls: ['./bookRide.component.css']
})
export class BookRideComponent implements OnInit {
  // Step control
  currentStep: 'location' | 'vehicle' | 'confirmation' = 'location';
  isLoading = false;

  // Inputs
  pickupLocation = '';
  destinationLocation = '';
  isScheduled = false;
  selectedDate = '';
  selectedTime = '';

  // Coordinates and distance
  pickupCoords: { lat: number, lng: number } | null = null;
  destinationCoords: { lat: number, lng: number } | null = null;
  estimatedDistanceKm = 0;
  estimatedDistanceText = '';
  estimatedDurationText = '';

  // Cabs list and selection
  allCabs: Cab[] = [];
  availableCabs: Cab[] = [];
  selectedCab: Cab | null = null;

  // Fare
  baseFare = 50; // configurable
  estimatedFare = 0;

  // Suggestions (static demo)
  locationSuggestions: LocationSuggestion[] = [
    { name: 'Mumbai Airport T1', address: 'Terminal 1, Andheri East, Mumbai', lat: 19.0896, lng: 72.8656 },
    { name: 'Mumbai Airport T2', address: 'Terminal 2, Andheri East, Mumbai', lat: 19.0896, lng: 72.8656 },
    { name: 'Chhatrapati Shivaji Terminus', address: 'CST, Fort, Mumbai', lat: 18.9398, lng: 72.8355 },
    { name: 'Bandra Kurla Complex', address: 'BKC, Bandra East, Mumbai', lat: 19.0728, lng: 72.8826 },
    { name: 'Powai Lake', address: 'Powai, Mumbai', lat: 19.1197, lng: 72.9073 },
    { name: 'Gateway of India', address: 'Apollo Bandar, Colaba, Mumbai', lat: 18.9220, lng: 72.8347 }
  ];

  constructor(private rideService: RideService) {}

  ngOnInit(): void {
    // Optional: prefill pickup from current location label
    this.pickupLocation = 'Current Location (Andheri West)';
  }

  // Select from suggestions
  selectLocationSuggestion(s: LocationSuggestion, type: 'pickup' | 'destination'): void {
    if (type === 'pickup') this.pickupLocation = s.name;
    else this.destinationLocation = s.name;
  }

  // First step: geocode and distance
  proceedToVehicleSelection(): void {
    if (!this.pickupLocation.trim() || !this.destinationLocation.trim()) {
      alert('Please enter both pickup and destination locations.');
      return;
    }
    this.isLoading = true;

    this.rideService.geocodeAddress(this.pickupLocation).subscribe({
      next: (p) => {
        this.pickupCoords = p;
        this.rideService.geocodeAddress(this.destinationLocation).subscribe({
          next: (d) => {
            this.destinationCoords = d;
            this.rideService.getDistance(p.lat, p.lng, d.lat, d.lng).subscribe({
              next: (km) => {
                this.estimatedDistanceKm = parseFloat(km.toFixed(1));
                this.estimatedDistanceText = `${this.estimatedDistanceKm} km`;
                // Fake duration estimate (simple heuristic)
                const minFrom = Math.floor(this.estimatedDistanceKm * 2 + 10);
                const minTo = Math.floor(this.estimatedDistanceKm * 3 + 15);
                this.estimatedDurationText = `${minFrom}-${minTo} min`;

                // Load available cabs now
                this.loadCabsForSelection();
              },
              error: () => this.fail('Failed to calculate distance.')
            });
          },
          error: () => this.fail('Failed to get destination coordinates.')
        });
      },
      error: () => this.fail('Failed to get pickup coordinates.')
    });
  }
private loadCabsForSelection(): void {
  this.rideService.listCabs().subscribe({
    next: (cabs) => {
      // Normalize to array, then filter availability
      this.allCabs = Array.isArray(cabs) ? cabs : [];
      this.availableCabs = this.allCabs.filter(c => c.isAvailable);

      // Pick the first available cab, or null if none
      this.selectedCab = this.availableCabs.length > 0 ? this.availableCabs[0] : null;

      // Update fare based on current selection
      this.recomputeFare();

      // Move to vehicle step
      this.isLoading = false;
      this.currentStep = 'vehicle';
    },
    error: (e) => {
      console.error('Error fetching cabs', e);
      this.fail('Failed to load available cars.');
    }
  });
}



  private fail(msg: string) {
    this.isLoading = false;
    alert(msg);
  }

  // Toggle schedule
  toggleScheduled(): void {
    this.isScheduled = !this.isScheduled;
    if (!this.isScheduled) {
      this.selectedDate = '';
      this.selectedTime = '';
    }
  }

  // Select cab card
  selectCab(cab: Cab): void {
    this.selectedCab = cab;
    this.recomputeFare();
  }

  private recomputeFare(): void {
    if (!this.selectedCab) {
      this.estimatedFare = 0;
      return;
    }
    // Fare = base + distance * perKmRate
    this.estimatedFare = Math.round(this.baseFare + this.estimatedDistanceKm * this.selectedCab.perKmRate);
  }

  proceedToConfirmation(): void {
    if (!this.selectedCab) {
      alert('Please select a car.');
      return;
    }
    this.currentStep = 'confirmation';
  }

  // Confirm booking
  confirmBooking(): void {
    if (!this.pickupCoords || !this.destinationCoords || !this.selectedCab) {
      alert('Missing trip details.');
      return;
    }
    this.isLoading = true;

    const scheduledTime = this.isScheduled && this.selectedDate && this.selectedTime
      ? `${this.selectedDate}T${this.selectedTime}:00`
      : null;

    const payload: BookRideRequest = {
      customerId: this.getCurrentCustomerId(),
      fromLocation: this.pickupLocation,
      toLocation: this.destinationLocation,
      distanceInKm: this.estimatedDistanceKm,
      carType: this.selectedCab.carType,
      fromLatitude: this.pickupCoords.lat,
      fromLongitude: this.pickupCoords.lng,
      scheduledTime
    };

    this.rideService.bookRide(payload).subscribe({
      next: (res: BookRideResponse) => {
        this.isLoading = false;
        alert(`Ride booked successfully! Booking ID: ${res.tripBookingId}`);
        this.resetBooking();
      },
      error: (err) => {
        console.error('Error booking ride', err);
        this.isLoading = false;
        alert('Failed to book ride. Please try again.');
      }
    });
  }

  // Demo auth retrieval
  private getCurrentCustomerId(): number {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        return user?.id;
      } catch {}
    }
    alert('Please log in to book a ride');
    throw new Error('User not authenticated');
  }

  // UI helpers
  goBack(): void {
    if (this.currentStep === 'vehicle') this.currentStep = 'location';
    else if (this.currentStep === 'confirmation') this.currentStep = 'vehicle';
  }

  resetBooking(): void {
    this.currentStep = 'location';
    this.pickupLocation = '';
    this.destinationLocation = '';
    this.isScheduled = false;
    this.selectedDate = '';
    this.selectedTime = '';
    this.pickupCoords = null;
    this.destinationCoords = null;
    this.estimatedDistanceKm = 0;
    this.estimatedDistanceText = '';
    this.estimatedDurationText = '';
    this.allCabs = [];
    this.availableCabs = [];
    this.selectedCab = null;
    this.estimatedFare = 0;
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }
}

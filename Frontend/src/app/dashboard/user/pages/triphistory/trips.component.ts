import { Component, OnInit } from '@angular/core';
import { RideService, BookRideResponse } from '../../../../core/services/user/ride.service';

interface Trip {
  tripBookingId: number;
  fromLocation: string;
  toLocation: string;
  fromDateTime: string;
  toDateTime: string | null;
  status: 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS';
  distanceInKm: number;
  bill: number;
  customerRating: number | null;
  driver: {
    id: number;
    username: string;
    mobileNumber: string | null;
    rating: number;
    profilePhotoUrl: string | null;
    licenceNo: string;
  };
  cab: {
    cabId: number;
    carType: string;
    numberPlate: string | ' ';
    imageUrl: string | null;
  };
}

@Component({
  selector: 'app-trip-history',
  standalone: false,
  templateUrl: './trips.component.html',
  styleUrls: ['./trips.component.css']
})
export class TripHistoryComponent implements OnInit {
  tripHistory: Trip[] = [];
  filteredTrips: Trip[] = [];
  selectedTrip: Trip | null = null;

  filterStatus: string = 'all';
  sortBy: string = 'date';
  searchTerm: string = '';

  showRatingModal: boolean = false;
  currentRating: number = 0;
  ratingTrip: Trip | null = null;

  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(public rideService: RideService) {}

  ngOnInit(): void {
    this.loadTripHistory();
  }

  private getCurrentUserId(): number | null {
    try {
      const raw = localStorage.getItem('currentUser'); // {"id":2,"email":"","username":"Nishant","role":"user","name":""}
      if (!raw) return null;
      const parsed = JSON.parse(raw);
      return typeof parsed?.id === 'number' ? parsed.id : null;
    } catch {
      return null;
    }
  }

  loadTripHistory(): void {
    this.isLoading = true;
    const customerId = this.getCurrentUserId();
    if (!customerId) {
      this.errorMessage = 'Missing current user. Please sign in again.';
      this.isLoading = false;
      return;
    }

    this.rideService.getCustomerTrips(customerId).subscribe({
      next: (trips: BookRideResponse[]) => {
        this.tripHistory = (trips || []).map(trip => this.mapApiResponseToTrip(trip));
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading trip history:', error);
        this.errorMessage = 'Failed to load trip history. Please try again later.';
        this.isLoading = false;
        this.initializeMockTripHistory();
      }
    });
  }

  mapApiResponseToTrip(api: BookRideResponse): Trip {
    const safeDriver = api?.driver ?? {
      id: 0,
      username: 'Assigned Soon',
      mobileNumber: null,
      rating: 0,
      profilePhotoUrl: null,
      licenceNo: 'N/A'
    };

    const safeCab = api?.cab
      ? {
          cabId: api.cab.cabId,
          carType: api.cab.carType ?? 'Vehicle',
          numberPlate: api.cab.numberPlate ?? ' ',
          imageUrl: api.cab.imageUrl ?? null
        }
      : {
          cabId: 0,
          carType: 'Vehicle',
          numberPlate: ' ',
          imageUrl: null
        };

    const normalizeStatus = (s: string): Trip['status'] => {
      switch ((s || '').toUpperCase()) {
        case 'COMPLETED': return 'COMPLETED';
        case 'CANCELLED': return 'CANCELLED';
        case 'IN_PROGRESS': return 'IN_PROGRESS';
        case 'CONFIRMED': return 'CONFIRMED';
        case 'SCHEDULED': return 'CONFIRMED'; // treat scheduled as confirmed/upcoming in UI
        default: return 'CONFIRMED';
      }
    };

    return {
      tripBookingId: api.tripBookingId,
      fromLocation: api.fromLocation,
      toLocation: api.toLocation,
      fromDateTime: api.fromDateTime,
      toDateTime: api.toDateTime ?? null,
      status: normalizeStatus(api.status),
      distanceInKm: api.distanceInKm,
      bill: api.bill,
      customerRating: api.customerRating ?? null,
      driver: {
        id: safeDriver.id,
        username: safeDriver.username,
        mobileNumber: safeDriver.mobileNumber ?? null,
        rating: safeDriver.rating ?? 0,
        profilePhotoUrl: safeDriver.profilePhotoUrl ?? null,
        licenceNo: safeDriver.licenceNo ?? 'N/A'
      },
      cab: safeCab
    };
  }

  initializeMockTripHistory(): void {
    this.tripHistory = [
      {
        tripBookingId: 2,
        fromLocation: 'Maharashtra',
        toLocation: 'Gujarat',
        fromDateTime: '2025-08-22T14:52:57.233009',
        toDateTime: '2025-08-22T14:57:13.276115',
        status: 'COMPLETED',
        distanceInKm: 700,
        bill: 10500,
        customerRating: null,
        driver: {
          id: 1,
          username: 'driver',
          mobileNumber: null,
          rating: 4.5,
          profilePhotoUrl: null,
          licenceNo: 'LIC123'
        },
        cab: {
          cabId: 1,
          carType: 'Sedan',
          numberPlate: 'MH12AB1234',
          imageUrl: null
        }
      },
      {
        tripBookingId: 1,
        fromLocation: 'Maharashtra',
        toLocation: 'Gujarat',
        fromDateTime: '2025-08-24T10:15:30',
        toDateTime: null,
        status: 'CONFIRMED',
        distanceInKm: 700,
        bill: 0,
        customerRating: null,
        driver: {
          id: 0,
          username: 'Assigned Soon',
          mobileNumber: null,
          rating: 0,
          profilePhotoUrl: null,
          licenceNo: 'N/A'
        },
        cab: {
          cabId: 0,
          carType: 'Vehicle',
          numberPlate: ' ',
          imageUrl: null
        }
      }
    ];
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.tripHistory];

    if (this.filterStatus !== 'all') {
      const statusMap: { [key: string]: string } = {
        'completed': 'COMPLETED',
        'ongoing': 'IN_PROGRESS',
        'cancelled': 'CANCELLED',
        'confirmed': 'CONFIRMED'
      };
      const targetStatus = statusMap[this.filterStatus] || this.filterStatus;
      filtered = filtered.filter(trip => trip.status === targetStatus);
    }

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(trip =>
        trip.fromLocation.toLowerCase().includes(term) ||
        trip.toLocation.toLowerCase().includes(term) ||
        trip.driver.username.toLowerCase().includes(term)
      );
    }

    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'date':
          return new Date(b.fromDateTime).getTime() - new Date(a.fromDateTime).getTime();
        case 'fare':
          return b.bill - a.bill;
        case 'rating':
          return (b.customerRating || 0) - (a.customerRating || 0);
        default:
          return 0;
      }
    });

    this.filteredTrips = filtered;
  }

  onFilterChange(): void { this.applyFilters(); }
  onSortChange(): void { this.applyFilters(); }
  onSearchChange(): void { this.applyFilters(); }

  viewTripDetails(trip: Trip): void {
    this.selectedTrip = trip;
  }
  closeTripDetails(): void {
    this.selectedTrip = null;
  }

  openRatingModal(trip: Trip): void {
    this.ratingTrip = trip;
    this.currentRating = trip.customerRating || 0;
    this.showRatingModal = true;
  }
  closeRatingModal(): void {
    this.showRatingModal = false;
    this.ratingTrip = null;
    this.currentRating = 0;
  }
  setRating(rating: number): void { this.currentRating = rating; }

  submitRating(): void {
    if (this.ratingTrip) {
      this.rideService.rateRide(this.ratingTrip.tripBookingId, this.currentRating).subscribe({
        next: () => {
          this.ratingTrip!.customerRating = this.currentRating;
          alert(`Rating submitted: ${this.currentRating} stars for ${this.ratingTrip!.driver.username}`);
          this.closeRatingModal();
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error submitting rating:', error);
          alert('Failed to submit rating. Please try again.');
        }
      });
    }
  }

  rebookTrip(trip: Trip): void {
    const confirmed = confirm(`Rebook trip from ${trip.fromLocation} to ${trip.toLocation}?`);
    if (confirmed) {
      alert('Redirecting to booking page with pre-filled details...');
    }
  }

  callDriver(trip: Trip): void {
    if (trip.driver.mobileNumber) {
      alert(`Calling ${trip.driver.username} at ${trip.driver.mobileNumber}`);
    } else {
      alert('Driver phone number not available');
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'status-completed';
      case 'IN_PROGRESS': return 'status-ongoing';
      case 'CONFIRMED': return 'status-confirmed';
      case 'CANCELLED': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'COMPLETED': return '✅';
      case 'IN_PROGRESS': return '🚗';
      case 'CONFIRMED': return '⏱️';
      case 'CANCELLED': return '❌';
      default: return '❓';
    }
  }

  getStatusDisplayText(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'Completed';
      case 'IN_PROGRESS': return 'Ongoing';
      case 'CONFIRMED': return 'Confirmed';
      case 'CANCELLED': return 'Cancelled';
      default: return status;
    }
  }

  getStarArray(rating: number): boolean[] {
    const stars: boolean[] = [];
    for (let i = 1; i <= 5; i++) stars.push(i <= rating);
    return stars;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }
  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  }
  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
}

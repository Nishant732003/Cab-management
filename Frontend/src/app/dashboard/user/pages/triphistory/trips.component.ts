import { Component, OnInit } from '@angular/core';
import { RideService, BookRideResponse } from '../../../../core/services/user/ride.service';
import { Router } from '@angular/router'; 
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

  constructor(
    public rideService: RideService,
     private router: Router 
  ) {}

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
mapApiResponseToTrip(api: any): Trip {
  // Create a safe driver object from the flat API response
  const safeDriver = {
    id: api.driverId || 0,
    username: `${api.driverFirstName || 'Driver'} ${api.driverLastName || ''}`.trim() || 'Assigned Soon',
    mobileNumber: null, // Not provided in API
    rating: 0, // Not provided in API
    profilePhotoUrl: null, // Not provided in API
    licenceNo: 'N/A' // Not provided in API
  };

  // Create a safe cab object from the flat API response
  const safeCab = {
    cabId: api.cabId || 0,
    carType: api.carType || 'Vehicle',
    numberPlate: ' ', // Not provided in API
    imageUrl: null // Not provided in API
  };

  // Normalize status
  const normalizeStatus = (s: string): Trip['status'] => {
    switch ((s || '').toUpperCase()) {
      case 'COMPLETED': return 'COMPLETED';
      case 'CANCELLED': return 'CANCELLED';
      case 'IN_PROGRESS': return 'IN_PROGRESS';
      case 'CONFIRMED': return 'CONFIRMED';
      case 'SCHEDULED': return 'CONFIRMED'; 
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
    distanceInKm: api.distanceinKm || 0, // Note: API uses 'distanceinKm' not 'distanceInKm'
    bill: api.bill || 0,
    customerRating: api.customerRating ?? null,
    driver: safeDriver,
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

  viewTripDetails(event:any,trip: Trip): void {
    const target = event.target as HTMLElement;
  const interactiveElements = ['BUTTON', 'A', 'INPUT', 'SELECT', 'TEXTAREA'];
  
  if (interactiveElements.includes(target.tagName) || 
      target.closest('button') || 
      target.closest('a')) {
    // Click was on an interactive element, don't open trip details
    return;
  }
  
     event.stopPropagation();
    this.selectedTrip = trip;
  }
  closeTripDetails(): void {
    this.selectedTrip = null;
  }

  openRatingModal(event:any,trip: Trip): void {
    event.stopPropagation();
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
      alert('Redirecting to booking page ...');
    }
      this.router.navigate(['/book-ride']);

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

 getStarArray(rating: number): {filled: boolean}[] {
  const stars: {filled: boolean}[] = [];
  for (let i = 1; i <= 5; i++) {
    stars.push({filled: i <= rating});
  }
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

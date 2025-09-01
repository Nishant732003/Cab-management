import { Component, OnInit } from '@angular/core';
import { RideService,BookRideResponse } from '../../../../core/services/user/ride.service';

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
    numberPlate: string;
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

  loadTripHistory(): void {
    this.isLoading = true;
    const customerId = 1;
    
    this.rideService.getCustomerTrips(customerId).subscribe({
      next: (trips: BookRideResponse[]) => {
        this.tripHistory = trips.map(trip => this.mapApiResponseToTrip(trip));
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error:any) => {
        console.error('Error loading trip history:', error);
        this.errorMessage = 'Failed to load trip history. Please try again later.';
        this.isLoading = false;
        
        // Fallback to mock data if API fails
        this.initializeMockTripHistory();
      }
    });
  }

  // Add this method to your RideService or create a new method if it doesn't exist
 
  mapApiResponseToTrip(apiResponse: BookRideResponse): Trip {
    return {
      tripBookingId: apiResponse.tripBookingId,
      fromLocation: apiResponse.fromLocation,
      toLocation: apiResponse.toLocation,
      fromDateTime: apiResponse.fromDateTime,
      toDateTime: apiResponse.toDateTime,
      status: apiResponse.status as 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS',
      distanceInKm: apiResponse.distanceInKm,
      bill: apiResponse.bill,
      customerRating: apiResponse.customerRating,
      driver: {
        id: apiResponse.driver.id,
        username: apiResponse.driver.username,
        mobileNumber: apiResponse.driver.mobileNumber,
        rating: apiResponse.driver.rating,
        profilePhotoUrl: apiResponse.driver.profilePhotoUrl,
        licenceNo: apiResponse.driver.licenceNo
      },
      cab: {
        cabId: apiResponse.cab.cabId,
        carType: apiResponse.cab.carType,
        numberPlate: apiResponse.cab.numberPlate,
        imageUrl: apiResponse.cab.imageUrl
      }
    };
  }

  initializeMockTripHistory(): void {
    // Fallback mock data based on the API response structure
    this.tripHistory = [
      {
        tripBookingId: 1,
        fromLocation: 'Mumbai',
        toLocation: 'Pune',
        fromDateTime: '2025-08-31T22:31:32.5751',
        toDateTime: null,
        status: 'CONFIRMED',
        distanceInKm: 150.0,
        bill: 0.0,
        customerRating: null,
        driver: {
          id: 1,
          username: 'Driver',
          mobileNumber: '+1-555-0101',
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
        tripBookingId: 2,
        fromLocation: 'Downtown Mall',
        toLocation: 'Airport Terminal 1',
        fromDateTime: '2024-01-15T14:30:00',
        toDateTime: '2024-01-15T15:00:00',
        status: 'COMPLETED',
        distanceInKm: 12.3,
        bill: 25.50,
        customerRating: 5,
        driver: {
          id: 2,
          username: 'John Smith',
          mobileNumber: '+1-555-0101',
          rating: 4.8,
          profilePhotoUrl: null,
          licenceNo: 'ABC-123'
        },
        cab: {
          cabId: 2,
          carType: 'Toyota Camry',
          numberPlate: 'ABC-123',
          imageUrl: null
        }
      }
    ];
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.tripHistory];

    // Filter by status
    if (this.filterStatus !== 'all') {
      const statusMap: {[key: string]: string} = {
        'completed': 'COMPLETED',
        'ongoing': 'IN_PROGRESS',
        'cancelled': 'CANCELLED'
      };
      
      const targetStatus = statusMap[this.filterStatus] || this.filterStatus;
      filtered = filtered.filter(trip => trip.status === targetStatus);
    }

    // Filter by search term
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(trip => 
        trip.fromLocation.toLowerCase().includes(term) ||
        trip.toLocation.toLowerCase().includes(term) ||
        trip.driver.username.toLowerCase().includes(term)
      );
    }

    // Sort trips
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

  onFilterChange(): void {
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

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

  setRating(rating: number): void {
    this.currentRating = rating;
  }

  submitRating(): void {
    if (this.ratingTrip) {
      // Call the API to submit the rating
      this.rideService.rateRide(this.ratingTrip.tripBookingId, this.currentRating).subscribe({
        next: () => {
          // Update local data on success
          this.ratingTrip!.customerRating = this.currentRating;
          alert(`Rating submitted: ${this.currentRating} stars for ${this.ratingTrip!.driver.username}`);
          this.closeRatingModal();
          this.applyFilters(); // Refresh the list
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
      // You would typically navigate to booking page with pre-filled data
    }
  }

  callDriver(trip: Trip): void {
    if (trip.driver.mobileNumber) {
      alert(`Calling ${trip.driver.username} at ${trip.driver.mobileNumber}`);
      // In a real app, you might use: window.open(`tel:${trip.driver.mobileNumber}`);
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
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating);
    }
    return stars;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
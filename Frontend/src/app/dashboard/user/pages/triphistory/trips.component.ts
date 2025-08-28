
import { Component, OnInit } from '@angular/core';

interface Driver {
  id: number;
  name: string;
  rating: number;
  phone: string;
  carModel: string;
  licensePlate: string;
  photo: string;
}

interface Trip {
  id: number;
  pickup: string;
  destination: string;
  date: string;
  time: string;
  driver: Driver;
  status: 'completed' | 'ongoing' | 'cancelled';
  fare: number;
  rating?: number;
  distance: string;
  duration: string;
  paymentMethod: string;
}

@Component({
  selector: 'app-trip-history',
  templateUrl: './trip-history.component.html',
  styleUrls: ['./trip-history.component.css']
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

  constructor() {}

  ngOnInit(): void {
    this.initializeTripHistory();
    this.applyFilters();
  }

  initializeTripHistory(): void {
    this.tripHistory = [
      {
        id: 1001,
        pickup: 'Downtown Mall',
        destination: 'Airport Terminal 1',
        date: '2024-01-15',
        time: '14:30',
        driver: {
          id: 1,
          name: 'John Smith',
          rating: 4.8,
          phone: '+1-555-0101',
          carModel: 'Toyota Camry',
          licensePlate: 'ABC-123',
          photo: '👨‍💼'
        },
        status: 'completed',
        fare: 25.50,
        rating: 5,
        distance: '12.3 km',
        duration: '18 mins',
        paymentMethod: 'Credit Card'
      },
      {
        id: 1002,
        pickup: 'Home',
        destination: 'Office Building',
        date: '2024-01-14',
        time: '08:15',
        driver: {
          id: 2,
          name: 'Sarah Johnson',
          rating: 4.9,
          phone: '+1-555-0102',
          carModel: 'Honda Civic',
          licensePlate: 'XYZ-789',
          photo: '👩‍💼'
        },
        status: 'completed',
        fare: 12.75,
        rating: 4,
        distance: '8.7 km',
        duration: '15 mins',
        paymentMethod: 'Cash'
      },
      {
        id: 1003,
        pickup: 'Restaurant Plaza',
        destination: 'City Center',
        date: '2024-01-13',
        time: '20:45',
        driver: {
          id: 3,
          name: 'Mike Davis',
          rating: 4.7,
          phone: '+1-555-0103',
          carModel: 'Ford Focus',
          licensePlate: 'DEF-456',
          photo: '👨‍🦲'
        },
        status: 'cancelled',
        fare: 0,
        distance: '5.2 km',
        duration: '0 mins',
        paymentMethod: 'Credit Card'
      },
      {
        id: 1004,
        pickup: 'Train Station',
        destination: 'University Campus',
        date: '2024-01-12',
        time: '16:20',
        driver: {
          id: 4,
          name: 'Emily Brown',
          rating: 4.6,
          phone: '+1-555-0104',
          carModel: 'Nissan Altima',
          licensePlate: 'GHI-789',
          photo: '👩‍🔬'
        },
        status: 'completed',
        fare: 18.25,
        distance: '15.1 km',
        duration: '22 mins',
        paymentMethod: 'Digital Wallet'
      },
      {
        id: 1005,
        pickup: 'Shopping Center',
        destination: 'Medical Clinic',
        date: '2024-01-11',
        time: '11:30',
        driver: {
          id: 5,
          name: 'David Wilson',
          rating: 4.5,
          phone: '+1-555-0105',
          carModel: 'Hyundai Elantra',
          licensePlate: 'JKL-012',
          photo: '👨‍⚕️'
        },
        status: 'completed',
        fare: 15.80,
        rating: 3,
        distance: '9.8 km',
        duration: '16 mins',
        paymentMethod: 'Credit Card'
      }
    ];
  }

  applyFilters(): void {
    let filtered = [...this.tripHistory];

    // Filter by status
    if (this.filterStatus !== 'all') {
      filtered = filtered.filter(trip => trip.status === this.filterStatus);
    }

    // Filter by search term
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(trip => 
        trip.pickup.toLowerCase().includes(term) ||
        trip.destination.toLowerCase().includes(term) ||
        trip.driver.name.toLowerCase().includes(term)
      );
    }

    // Sort trips
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'date':
          return new Date(b.date + ' ' + b.time).getTime() - new Date(a.date + ' ' + a.time).getTime();
        case 'fare':
          return b.fare - a.fare;
        case 'rating':
          return (b.rating || 0) - (a.rating || 0);
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
    this.currentRating = trip.rating || 0;
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
      this.ratingTrip.rating = this.currentRating;
      // Here you would typically send the rating to your backend
      alert(`Rating submitted: ${this.currentRating} stars for ${this.ratingTrip.driver.name}`);
      this.closeRatingModal();
      this.applyFilters(); // Refresh the list
    }
  }

  rebookTrip(trip: Trip): void {
    const confirmed = confirm(`Rebook trip from ${trip.pickup} to ${trip.destination}?`);
    if (confirmed) {
      alert('Redirecting to booking page with pre-filled details...');
    }
  }

  callDriver(trip: Trip): void {
    alert(`Calling ${trip.driver.name} at ${trip.driver.phone}`);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'completed': return 'status-completed';
      case 'ongoing': return 'status-ongoing';
      case 'cancelled': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'completed': return '✅';
      case 'ongoing': return '🚗';
      case 'cancelled': return '❌';
      default: return '❓';
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

  formatTime(timeString: string): string {
    const [hours, minutes] = timeString.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
  }
}

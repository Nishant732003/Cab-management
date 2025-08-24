import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Trip, TripStatus, PaymentMethod, Location } from '../../../../core/models/driver/driver.model';

interface TripFilter {
  dateRange: 'today' | 'week' | 'month' | 'custom';
  status: TripStatus | 'all';
  paymentMethod: PaymentMethod | 'all';
  minFare?: number;
  maxFare?: number;
  customStartDate?: Date;
  customEndDate?: Date;
}

interface TripStats {
  totalTrips: number;
  totalEarnings: number;
  totalDistance: number;
  totalTips: number;
  avgRating: number;
  completedTrips: number;
  cancelledTrips: number;
}

@Component({
  selector: 'app-driver-trips',
  templateUrl: './trip.component.html',
  styleUrls: ['./trip.component.css'],
  standalone:false
})
export class TripsComponent implements OnInit {

  // Trip data
  allTrips: Trip[] = [];
  filteredTrips: Trip[] = [];
  selectedTrip: Trip | null = null;
  
  // Filters and search
  searchTerm = '';
  sortBy: 'date' | 'fare' | 'distance' | 'rating' = 'date';
  sortOrder: 'asc' | 'desc' = 'desc';
  
  filters: TripFilter = {
    dateRange: 'week',
    status: 'all',
    paymentMethod: 'all'
  };

  // Statistics
  tripStats: TripStats = {
    totalTrips: 0,
    totalEarnings: 0,
    totalDistance: 0,
    totalTips: 0,
    avgRating: 0,
    completedTrips: 0,
    cancelledTrips: 0
  };

  // UI state
  showFilters = false;
  isLoading = false;
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Enums for template
  TripStatus = TripStatus;
  PaymentMethod = PaymentMethod;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.initializeMockData();
    this.applyFilters();
    this.calculateStats();
  }

  initializeMockData(): void {
    // Generate mock trip data
    this.allTrips = [
      {
        id: '1',
        driverId: '1',
        passengerId: 'p1',
        passengerName: 'Alice Johnson',
        passengerPhone: '+1-555-0001',
        pickupLocation: {
          latitude: 28.6139,
          longitude: 77.2090,
          address: '123 Main St, Connaught Place',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6500,
          longitude: 77.2300,
          address: '456 Oak Ave, Karol Bagh',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 2 * 60 * 60 * 1000 + 15 * 60 * 1000),
        distance: 5.2,
        duration: 15,
        fare: 120.50,
        tip: 15.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.UPI,
        rating: 5
      },
      {
        id: '2',
        driverId: '1',
        passengerId: 'p2',
        passengerName: 'Raj Sharma',
        passengerPhone: '+91-9876543210',
        pickupLocation: {
          latitude: 28.6000,
          longitude: 77.2100,
          address: '789 Pine Rd, Lajpat Nagar',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6200,
          longitude: 77.2400,
          address: '321 Elm St, Defence Colony',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 4 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 4 * 60 * 60 * 1000 + 22 * 60 * 1000),
        distance: 7.8,
        duration: 22,
        fare: 185.75,
        tip: 20.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CARD,
        rating: 4
      },
      {
        id: '3',
        driverId: '1',
        passengerId: 'p3',
        passengerName: 'Priya Gupta',
        passengerPhone: '+91-9123456789',
        pickupLocation: {
          latitude: 28.5800,
          longitude: 77.1900,
          address: '555 Cedar Blvd, Vasant Kunj',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.7000,
          longitude: 77.2500,
          address: '888 Maple Dr, Rohini',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 24 * 60 * 60 * 1000 + 35 * 60 * 1000),
        distance: 15.3,
        duration: 35,
        fare: 295.25,
        tip: 25.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CASH,
        rating: 5
      },
      {
        id: '4',
        driverId: '1',
        passengerId: 'p4',
        passengerName: 'Amit Kumar',
        passengerPhone: '+91-9988776655',
        pickupLocation: {
          latitude: 28.6600,
          longitude: 77.2200,
          address: '111 Oak St, Chandni Chowk',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.5500,
          longitude: 77.2600,
          address: '222 Pine Ave, Greater Kailash',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 + 28 * 60 * 1000),
        distance: 12.7,
        duration: 28,
        fare: 240.00,
        tip: 0,
        status: TripStatus.CANCELLED,
        paymentMethod: PaymentMethod.UPI,
        rating: 0
      },
      {
        id: '5',
        driverId: '1',
        passengerId: 'p5',
        passengerName: 'Sunita Devi',
        passengerPhone: '+91-9567891234',
        pickupLocation: {
          latitude: 28.6300,
          longitude: 77.2150,
          address: '333 Birch Rd, India Gate',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6800,
          longitude: 77.2000,
          address: '444 Walnut St, Civil Lines',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000 + 18 * 60 * 1000),
        distance: 8.9,
        duration: 18,
        fare: 165.50,
        tip: 10.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.WALLET,
        rating: 4
      }
    ];
  }

  applyFilters(): void {
    let filtered = [...this.allTrips];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(trip => 
        trip.passengerName.toLowerCase().includes(term) ||
        trip.pickupLocation.address.toLowerCase().includes(term) ||
        trip.dropLocation.address.toLowerCase().includes(term) ||
        trip.id.toLowerCase().includes(term)
      );
    }

    // Date range filter
    const now = new Date();
    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const startOfWeek = new Date(startOfToday.getTime() - (startOfToday.getDay() * 24 * 60 * 60 * 1000));
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);

    switch (this.filters.dateRange) {
      case 'today':
        filtered = filtered.filter(trip => trip.startTime >= startOfToday);
        break;
      case 'week':
        filtered = filtered.filter(trip => trip.startTime >= startOfWeek);
        break;
      case 'month':
        filtered = filtered.filter(trip => trip.startTime >= startOfMonth);
        break;
      case 'custom':
        if (this.filters.customStartDate && this.filters.customEndDate) {
          filtered = filtered.filter(trip => 
            trip.startTime >= this.filters.customStartDate! && 
            trip.startTime <= this.filters.customEndDate!
          );
        }
        break;
    }

    // Status filter
    if (this.filters.status !== 'all') {
      filtered = filtered.filter(trip => trip.status === this.filters.status);
    }

    // Payment method filter
    if (this.filters.paymentMethod !== 'all') {
      filtered = filtered.filter(trip => trip.paymentMethod === this.filters.paymentMethod);
    }

    // Fare range filter
    if (this.filters.minFare !== undefined) {
      filtered = filtered.filter(trip => trip.fare >= this.filters.minFare!);
    }
    if (this.filters.maxFare !== undefined) {
      filtered = filtered.filter(trip => trip.fare <= this.filters.maxFare!);
    }

    // Sort
    filtered.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.sortBy) {
        case 'date':
          aValue = a.startTime.getTime();
          bValue = b.startTime.getTime();
          break;
        case 'fare':
          aValue = a.fare;
          bValue = b.fare;
          break;
        case 'distance':
          aValue = a.distance;
          bValue = b.distance;
          break;
        case 'rating':
          aValue = a.rating;
          bValue = b.rating;
          break;
      }

      if (this.sortOrder === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });

    this.filteredTrips = filtered;
    this.totalPages = Math.ceil(this.filteredTrips.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  calculateStats(): void {
    const completedTrips = this.filteredTrips.filter(trip => trip.status === TripStatus.COMPLETED);
    
    this.tripStats = {
      totalTrips: this.filteredTrips.length,
      totalEarnings: completedTrips.reduce((sum, trip) => sum + trip.fare, 0),
      totalDistance: completedTrips.reduce((sum, trip) => sum + trip.distance, 0),
      totalTips: completedTrips.reduce((sum, trip) => sum + trip.tip, 0),
      avgRating: completedTrips.length > 0 
        ? completedTrips.reduce((sum, trip) => sum + trip.rating, 0) / completedTrips.length 
        : 0,
      completedTrips: completedTrips.length,
      cancelledTrips: this.filteredTrips.filter(trip => trip.status === TripStatus.CANCELLED).length
    };
  }

  get paginatedTrips(): Trip[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredTrips.slice(startIndex, endIndex);
  }

  onSearch(): void {
    this.applyFilters();
    this.calculateStats();
  }

  onFilterChange(): void {
    this.applyFilters();
    this.calculateStats();
  }

  onSortChange(sortBy: 'date' | 'fare' | 'distance' | 'rating'): void {
    if (this.sortBy === sortBy) {
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = sortBy;
      this.sortOrder = 'desc';
    }
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  clearFilters(): void {
    this.filters = {
      dateRange: 'week',
      status: 'all',
      paymentMethod: 'all'
    };
    this.searchTerm = '';
    this.applyFilters();
    this.calculateStats();
  }

  selectTrip(trip: Trip): void {
    this.selectedTrip = trip;
  }

  closeModal(): void {
    this.selectedTrip = null;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString('en-IN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
  }

  getStatusBadgeClass(status: TripStatus): string {
    switch (status) {
      case TripStatus.COMPLETED:
        return 'status-completed';
      case TripStatus.CANCELLED:
        return 'status-cancelled';
      case TripStatus.IN_PROGRESS:
        return 'status-progress';
      default:
        return 'status-default';
    }
  }

  getPaymentMethodIcon(method: PaymentMethod): string {
    switch (method) {
      case PaymentMethod.CASH:
        return '💵';
      case PaymentMethod.CARD:
        return '💳';
      case PaymentMethod.UPI:
        return '📱';
      case PaymentMethod.WALLET:
        return '👛';
      default:
        return '💰';
    }
  }

  exportTrips(): void {
    // Mock export functionality
    console.log('Exporting trips...', this.filteredTrips);
    alert('Trip export feature will be available soon!');
  }

  goBack(): void {
    this.router.navigate(['/driver/overview']);
  }
}
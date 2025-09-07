import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil, debounceTime, distinctUntilChanged, Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Trip, TripStatus, PaymentMethod, Location } from '../../../../core/models/driver/driver.model';
import { DriverService, Trip as ApiTrip } from '../../../../core/services/driver/driver.service';

interface TripFilter {
  dateRange: 'today' | 'week' | 'month' | 'custom'| 'all';
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
  standalone: false,
})
export class TripsComponent implements OnInit, OnDestroy {
  // Observables
  private readonly destroy$ = new Subject<void>();
  private readonly searchSubject = new Subject<string>();
   isUpdatingStatus = false;
   statusUpdateMessage = '';
  // Trip data
  allTrips: Trip[] = [];
  filteredTrips: Trip[] = [];
  selectedTrip: Trip | null = null;
  
  // Filters and search
  searchTerm = '';
  sortBy: 'date' | 'fare' | 'distance' | 'rating' = 'date';
  sortOrder: 'asc' | 'desc' = 'desc';
  
  filters: TripFilter = {
    dateRange: 'all',
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
  error: string | null = null;

  // Driver ID
  private driverId: string | null = null;
  TripStatus = TripStatus;
  PaymentMethod = PaymentMethod;

  constructor(
    private router: Router,
    private driverService: DriverService
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
    this.setupSearchDebounce();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeComponent(): void {
    this.getDriverIdFromCurrentUser();
    if (this.driverId) {
      this.loadTrips();
    } else {
      this.handleNoDriverId();
    }
  }

  private setupSearchDebounce(): void {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.applyFilters();
        this.calculateStats();
      });
  }

  private getDriverIdFromCurrentUser(): void {
    try {
      const userStr = localStorage.getItem('currentUser');
      
      if (!userStr) {
        this.error = 'User not authenticated';
        return;
      }

      const user = JSON.parse(userStr);
      
      if (!user?.id) {
        this.error = 'Invalid user data';
        return;
      }

      this.driverId = user.id.toString();
    } catch (error) {
      console.error('Error parsing user data:', error);
      this.error = 'Failed to parse user data';
    }
  }

  private handleNoDriverId(): void {
    this.error = 'Driver ID not found. Using demo data.';
    this.initializeMockData();
    this.applyFilters();
    this.calculateStats();
  }

  loadTrips(): void {
    if (!this.driverId) {
      this.error = 'Driver ID not available';
      return;
    }
    
    this.isLoading = true;
    this.error = null;

    this.driverService.getTripsForDriver(Number(this.driverId))
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (apiTrips: ApiTrip[]) => {
          this.allTrips = this.transformApiTripsToTrips(apiTrips);
          this.applyFilters();
          this.calculateStats();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading trips:', error);
          this.error = 'Failed to load trips. Please try again.';
          this.isLoading = false;
          this.initializeMockData();
          this.applyFilters();
          this.calculateStats();
        }
      });
  }
  
  private transformApiTripsToTrips(apiTrips: ApiTrip[]): Trip[] {
    return apiTrips.map((apiTrip) => {
      const start = apiTrip.fromDateTime ? new Date(apiTrip.fromDateTime) : new Date();
      const end = apiTrip.toDateTime ? new Date(apiTrip.toDateTime) : start;

      return {
        id: String(apiTrip.tripBookingId),
        passengerName: `${apiTrip.customerFirstName || ''} ${apiTrip.customerLastName || ''}`.trim() || 'Customer',
        pickupLocation: {
          address: apiTrip.fromLocation || '',
          latitude: 0,
          longitude: 0,
          city: this.extractCityFromAddress(apiTrip.fromLocation || ''),
          state: 'Unknown',
        },
        dropLocation: {
          address: apiTrip.toLocation || '',
          latitude: 0,
          longitude: 0,
          city: this.extractCityFromAddress(apiTrip.toLocation || ''),
          state: 'Unknown',
        },
        fare: Number(apiTrip.bill ?? 0),
        rating: Number(apiTrip.customerRating ?? 0),
        status: this.mapApiStatusToTripStatus(apiTrip.status || 'confirmed'),
        distanceinKm: Number(apiTrip?.distanceinKm) || 0, // ✅ Fixed: using distanceinKm
        
        // Use dummy data for other fields not in the API response
        driverId: this.driverId || 'unknown',
        passengerId: 'unknown',
        passengerPhone: '',
        startTime: start,
        endTime: end,
        duration: this.calculateDuration(start.toISOString(), end.toISOString()),
        tip: 0,
        paymentMethod: PaymentMethod.CASH,
      };
    });
  }
  
  private extractCityFromAddress(address: string): string {
    if (!address) return 'Unknown';
    const parts = address.split(',');
    return parts.length > 1 ? parts[parts.length - 1].trim() : 'Unknown';
  }

  private calculateDuration(startDateTime: string, endDateTime: string | null): number {
    if (!endDateTime) return 0;
    
    const start = new Date(startDateTime);
    const end = new Date(endDateTime);
    const diffMs = end.getTime() - start.getTime();
    return Math.round(diffMs / (1000 * 60));
  }

  private mapApiStatusToTripStatus(apiStatus: string): TripStatus {
    if (!apiStatus) return TripStatus.COMPLETED;
    
    switch (apiStatus.toLowerCase()) {
      case 'completed':
        return TripStatus.COMPLETED;
      case 'cancelled':
      case 'canceled':
        return TripStatus.CANCELLED;
      case 'confirmed':
        return TripStatus.CONFIRMED;
      case 'in_progress':
      case 'ongoing':
      case 'active':
        return TripStatus.IN_PROGRESS;
      default:
        return TripStatus.COMPLETED;
    }
  }

  private mapApiPaymentMethod(apiPaymentMethod: string | undefined): PaymentMethod | null {
    if (!apiPaymentMethod) return null;
    
    switch (apiPaymentMethod.toLowerCase()) {
      case 'cash':
        return PaymentMethod.CASH;
      case 'card':
      case 'credit_card':
      case 'debit_card':
        return PaymentMethod.CARD;
      case 'upi':
        return PaymentMethod.UPI;
      case 'wallet':
        return PaymentMethod.WALLET;
      default:
        return PaymentMethod.UPI;
    }
  }

  initializeMockData(): void {
    const now = new Date();
    
    this.allTrips = [
      {
        id: 'T001',
        driverId: this.driverId || '1',
        passengerId: 'p1',
        passengerName: 'Rajesh Kumar',
        passengerPhone: '+91-98765-43210',
        pickupLocation: {
          latitude: 28.6139,
          longitude: 77.2090,
          address: 'Connaught Place, New Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6129,
          longitude: 77.2295,
          address: 'India Gate, New Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(now.getTime() - 2 * 60 * 60 * 1000),
        endTime: new Date(now.getTime() - 2 * 60 * 60 * 1000 + 25 * 60 * 1000),
        distanceinKm: 8.5, // ✅ Fixed: using distanceinKm instead of distance
        duration: 25,
        fare: 185.50,
        tip: 20.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.UPI,
        rating: 5
      },
      {
        id: 'T002',
        driverId: this.driverId || '1',
        passengerId: 'p2',
        passengerName: 'Priya Sharma',
        passengerPhone: '+91-98765-43211',
        pickupLocation: {
          latitude: 28.6562,
          longitude: 77.2410,
          address: 'Karol Bagh Metro Station, New Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.5245,
          longitude: 77.2066,
          address: 'Select City Walk Mall, Saket',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(now.getTime() - 5 * 60 * 60 * 1000),
        endTime: new Date(now.getTime() - 5 * 60 * 60 * 1000 + 42 * 60 * 1000),
        distanceinKm: 14.2, // ✅ Fixed: using distanceinKm instead of distance
        duration: 42,
        fare: 245.00,
        tip: 0,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CARD,
        rating: 4
      },
      {
        id: 'T003',
        driverId: this.driverId || '1',
        passengerId: 'p3',
        passengerName: 'Amit Verma',
        passengerPhone: '+91-98765-43212',
        pickupLocation: {
          latitude: 28.4595,
          longitude: 77.0266,
          address: 'Cyber City, Gurgaon',
          city: 'Gurgaon',
          state: 'Haryana'
        },
        dropLocation: {
          latitude: 28.4089,
          longitude: 77.3178,
          address: 'IGI Airport Terminal 3, Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(now.getTime() - 24 * 60 * 60 * 1000),
        endTime: new Date(now.getTime() - 24 * 60 * 60 * 1000 + 65 * 60 * 1000),
        distanceinKm: 32.5, 
        duration: 65,
        fare: 450.00,
        tip: 50.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CASH,
        rating: 5
      },
      {
        id: 'T004',
        driverId: this.driverId || '1',
        passengerId: 'p4',
        passengerName: 'Neha Singh',
        passengerPhone: '+91-98765-43213',
        pickupLocation: {
          latitude: 28.7041,
          longitude: 77.1025,
          address: 'Chandni Chowk, Old Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6139,
          longitude: 77.2090,
          address: 'Connaught Place, New Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(now.getTime() - 26 * 60 * 60 * 1000),
        endTime: new Date(now.getTime() - 26 * 60 * 60 * 1000 + 15 * 60 * 1000),
        distanceinKm: 5.1,
        duration: 15,
        fare: 95.00,
        tip: 0,
        status: TripStatus.CANCELLED,
        paymentMethod: PaymentMethod.CASH,
        rating: 0
      },
      {
        id: 'T005',
        driverId: this.driverId || '1',
        passengerId: 'p5',
        passengerName: 'Rohit Gupta',
        passengerPhone: '+91-98765-43214',
        pickupLocation: {
          latitude: 28.5355,
          longitude: 77.3910,
          address: 'Noida City Centre, Noida',
          city: 'Noida',
          state: 'UP'
        },
        dropLocation: {
          latitude: 28.6139,
          longitude: 77.2090,
          address: 'India Gate, New Delhi',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(now.getTime() - 48 * 60 * 60 * 1000),
        endTime: new Date(now.getTime() - 48 * 60 * 60 * 1000 + 55 * 60 * 1000),
        distanceinKm: 25.8, 
        duration: 55,
        fare: 350.00,
        tip: 25.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.UPI,
        rating: 4.5
      }
    ];
  }

  applyFilters(): void {
    let filtered = [...this.allTrips];

    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(trip => 
        trip.passengerName.toLowerCase().includes(term) ||
        trip.pickupLocation.address.toLowerCase().includes(term) ||
        trip.dropLocation.address.toLowerCase().includes(term) ||
        trip.id.toLowerCase().includes(term)
      );
    }

    // Date range filter
    filtered = this.applyDateFilter(filtered);

    // Status filter
    if (this.filters.status !== 'all') {
      filtered = filtered.filter(trip => trip.status === this.filters.status);
    }

    // Payment method filter
    if (this.filters.paymentMethod !== 'all') {
      filtered = filtered.filter(trip => trip.paymentMethod === this.filters.paymentMethod);
    }

    // Fare range filter
    if (this.filters.minFare !== undefined && this.filters.minFare > 0) {
      filtered = filtered.filter(trip => trip.fare >= this.filters.minFare!);
    }
    if (this.filters.maxFare !== undefined && this.filters.maxFare > 0) {
      filtered = filtered.filter(trip => trip.fare <= this.filters.maxFare!);
    }

    // Sort
    filtered = this.applySorting(filtered);

    this.filteredTrips = filtered;
    this.updatePagination();
  }

  private applyDateFilter(trips: Trip[]): Trip[] {
    const now = new Date();
    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const startOfWeek = new Date(startOfToday.getTime() - (startOfToday.getDay() * 24 * 60 * 60 * 1000));
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);

    switch (this.filters.dateRange) {
      case 'today':
        return trips.filter(trip => trip.startTime >= startOfToday);
      case 'week':
        return trips.filter(trip => trip.startTime >= startOfWeek);
      case 'month':
        return trips.filter(trip => trip.startTime >= startOfMonth);
      case 'custom':
        if (this.filters.customStartDate && this.filters.customEndDate) {
          const endDate = new Date(this.filters.customEndDate);
          endDate.setHours(23, 59, 59, 999);
          
          return trips.filter(trip => 
            trip.startTime >= this.filters.customStartDate! && 
            trip.startTime <= endDate
          );
        }
        return trips;
      default:
        return trips;
    }
  }

  private applySorting(trips: Trip[]): Trip[] {
    return trips.sort((a, b) => {
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
          aValue = a.distanceinKm; 
          bValue = b.distanceinKm; 
          break;
        case 'rating':
          aValue = a.rating;
          bValue = b.rating;
          break;
        default:
          aValue = a.startTime.getTime();
          bValue = b.startTime.getTime();
      }

      const comparison = aValue < bValue ? -1 : aValue > bValue ? 1 : 0;
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
  }

  private updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredTrips.length / this.itemsPerPage) || 1;
    this.currentPage = Math.min(this.currentPage, this.totalPages);
    if (this.currentPage < 1) this.currentPage = 1;
  }

  calculateStats(): void {
    const completedTrips = this.filteredTrips.filter(trip => trip.status === TripStatus.COMPLETED);
    const ratingsSum = completedTrips.reduce((sum, trip) => sum + trip.rating, 0);
    const ratingsCount = completedTrips.filter(trip => trip.rating > 0).length;
    
    this.tripStats = {
      totalTrips: this.filteredTrips.length,
      totalEarnings: completedTrips.reduce((sum, trip) => sum + trip.fare + trip.tip, 0),
      totalDistance: completedTrips.reduce((sum, trip) => sum + trip.distanceinKm, 0), // ✅ Fixed: using distanceinKm
      totalTips: completedTrips.reduce((sum, trip) => sum + trip.tip, 0),
      avgRating: ratingsCount > 0 ? Number((ratingsSum / ratingsCount).toFixed(1)) : 0,
      completedTrips: completedTrips.length,
      cancelledTrips: this.filteredTrips.filter(trip => trip.status === TripStatus.CANCELLED).length
    };
  }

  get paginatedTrips(): Trip[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredTrips.slice(startIndex, endIndex);
  }

  // Event handlers
  onSearch(): void {
    this.searchSubject.next(this.searchTerm);
  }

  onFilterChange(): void {
    this.currentPage = 1; 
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
    this.currentPage = 1;
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

  goToPage(page: number | string): void {
    if (typeof page === 'number' && page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  refreshTrips(): void {
    this.currentPage = 1;
    this.loadTrips();
  }

  // Utility methods
  formatTime(date: Date): string {
    if (!(date instanceof Date) || isNaN(date.getTime())) {
      return '--:--';
    }
    
    return date.toLocaleTimeString('en-IN', { 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: true
    });
  }

  formatDate(date: Date): string {
    if (!(date instanceof Date) || isNaN(date.getTime())) {
      return 'Invalid Date';
    }
    
    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  formatDuration(minutes: number): string {
    if (minutes === null || minutes < 0) return '0m';
    
    const hours = Math.floor(minutes / 60);
    const mins = Math.round(minutes % 60);
    if (hours > 0 && mins > 0) return `${hours}h ${mins}m`;
    if (hours > 0) return `${hours}h`;
    return `${mins}m`;
  }

  getStatusBadgeClass(status: TripStatus): string {
    switch (status) {
      case TripStatus.COMPLETED:
        return 'status-completed';
      case TripStatus.CANCELLED:
        return 'status-cancelled';
      case TripStatus.IN_PROGRESS:
        return 'status-progress';
      case TripStatus.CONFIRMED:
          return 'status-confirmed';
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
    try {
      const csvContent = this.generateCSV();
      this.downloadCSV(csvContent, `trips-export-${this.formatDate(new Date())}.csv`);
    } catch (error) {
      console.error('Error exporting trips:', error);
    }
  }

  private generateCSV(): string {
    const headers = [
      'Trip ID', 'Passenger', 'Phone', 'Pickup', 'Drop-off', 
      'Date', 'Time', 'Distance (km)', 'Duration', 'Fare (₹)', 
      'Tip (₹)', 'Total (₹)', 'Status', 'Payment Method', 'Rating'
    ];
    
    const rows = this.filteredTrips.map(trip => [
      trip.id,
      trip.passengerName,
      trip.passengerPhone,
      trip.pickupLocation.address,
      trip.dropLocation.address,
      this.formatDate(trip.startTime),
      this.formatTime(trip.startTime),
      trip.distanceinKm.toFixed(1), // ✅ Fixed: using distanceinKm
      this.formatDuration(trip.duration),
      trip.fare.toFixed(2),
      trip.tip.toFixed(2),
      (trip.fare + trip.tip).toFixed(2),
      trip.status,
      trip.paymentMethod,
      trip.rating.toString()
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.map(cell => `"${cell.toString().replace(/"/g, '""')}"`).join(','))
      .join('\n');

    return csvContent;
  }

  private downloadCSV(content: string, filename: string): void {
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    }
  }

  goBack(): void {
    this.router.navigate(['/driver/overview']);
  }

  // Track by functions for Angular performance
  trackByTripId(index: number, trip: Trip): string {
    return trip.id;
  }

  trackByPage(index: number, page: number | string): number | string {
    return page;
  }

  getVisiblePages(): (number | string)[] {
    const pages: (number | string)[] = [];
    const maxPagesToShow = 5;
    const startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
    const endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    if (startPage > 1) {
      pages.push(1);
      if (startPage > 2) {
        pages.push('...');
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    if (endPage < this.totalPages) {
      if (endPage < this.totalPages - 1) {
        pages.push('...');
      }
      pages.push(this.totalPages);
    }

    return pages;
  }

  // Validation methods
  private validateDateRange(): boolean {
    if (this.filters.dateRange === 'custom') {
      if (!this.filters.customStartDate || !this.filters.customEndDate) {
        return false;
      }
      return this.filters.customStartDate <= this.filters.customEndDate;
    }
    return true;
  }

  private validateFareRange(): boolean {
    if (this.filters.minFare !== undefined && this.filters.maxFare !== undefined) {
      return this.filters.minFare <= this.filters.maxFare;
    }
    return true;
  }

  // Error handling
  private handleError(error: any, context: string): void {
    console.error(`Error in ${context}:`, error);
    this.error = `Failed to ${context}. Please try again.`;
  }

  // Accessibility helpers
  getAriaLabel(trip: Trip): string {
    return `Trip ${trip.id} with ${trip.passengerName} from ${trip.pickupLocation.address} to ${trip.dropLocation.address} on ${this.formatDate(trip.startTime)} for ₹${trip.fare}`;
  }

  getStatusAriaLabel(status: TripStatus): string {
    switch (status) {
      case TripStatus.COMPLETED:
        return 'Trip completed successfully';
      case TripStatus.CANCELLED:
        return 'Trip was cancelled';
      case TripStatus.IN_PROGRESS:
        return 'Trip is currently in progress';
      case TripStatus.CONFIRMED:
          return 'Trip is confirmed and waiting for pickup';
      default:
        return 'Trip status unknown';
    }
  }

  startTrip(tripId: string): void {
  this.updateTripStatus(tripId, TripStatus.IN_PROGRESS, 'Trip started successfully');
}

completeTrip(tripId: string): void {
  this.updateTripStatus(tripId, TripStatus.COMPLETED, 'Trip completed successfully');
}

cancelTrip(tripId: string): void {
  if (confirm('Are you sure you want to cancel this trip?')) {
    this.updateTripStatus(tripId, TripStatus.CANCELLED, 'Trip cancelled successfully');
  }
}

private updateTripStatus(tripId: string, status: TripStatus, successMessage: string): void {
  this.isUpdatingStatus = true;
  this.statusUpdateMessage = '';
  
  // Convert tripId to number if your API expects a number
  const numericTripId = Number(tripId);
  
  let apiCall: Observable<any>;
  
  switch (status) {
    case TripStatus.IN_PROGRESS:
      apiCall = this.driverService.startTrip(numericTripId);
      break;
    case TripStatus.COMPLETED:
      apiCall = this.driverService.completeTrip(numericTripId);
      break;
    case TripStatus.CANCELLED:
      apiCall = this.driverService.cancelTrip(numericTripId);
      break;
    default:
      console.error('Invalid status update');
      this.isUpdatingStatus = false;
      return;
  }
  
  apiCall.pipe(takeUntil(this.destroy$)).subscribe({
    next: (updatedTrip) => {
      // Update the trip in our local data
      const index = this.allTrips.findIndex(t => t.id === tripId);
      if (index !== -1) {
        this.allTrips[index].status = status;
      }
      
      // Also update the selected trip if it's the same one
      if (this.selectedTrip && this.selectedTrip.id === tripId) {
        this.selectedTrip.status = status;
      }
      
      this.statusUpdateMessage = successMessage;
      this.isUpdatingStatus = false;
      
      // Recalculate stats and refresh filters
      this.applyFilters();
      this.calculateStats();
      
      // Clear message after 3 seconds
      setTimeout(() => {
        this.statusUpdateMessage = '';
      }, 3000);
    },
    error: (error) => {
      console.error('Error updating trip status:', error);
      this.statusUpdateMessage = 'Failed to update trip status. Please try again.';
      this.isUpdatingStatus = false;
      
      // Clear error message after 5 seconds
      setTimeout(() => {
        this.statusUpdateMessage = '';
      }, 5000);
    }
  });
}
}
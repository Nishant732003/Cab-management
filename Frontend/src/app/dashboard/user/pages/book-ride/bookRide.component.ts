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

  // Form inputs
  pickupLocation = '';
  destinationLocation = '';
  isScheduled = false;
  selectedDate = '';
  selectedTime = '';

  // Location coordinates and trip data
  pickupCoords: { lat: number, lng: number } | null = null;
  destinationCoords: { lat: number, lng: number } | null = null;
  estimatedDistanceKm = 0;
  estimatedDistanceText = '';
  estimatedDurationText = '';

  // Vehicle data
  allCabs: Cab[] = [];
  availableCabs: Cab[] = [];
  selectedCab: Cab | null = null;

  // Pricing
  baseFare = 50; // Base fare in INR (configurable)
  estimatedFare = 0;

  // Location suggestions (demo data)
  locationSuggestions: LocationSuggestion[] = [
    // { 
    //   name: 'Mumbai Airport Terminal 1', 
    //   address: 'Chhatrapati Shivaji International Airport, Andheri East, Mumbai', 
    //   lat: 19.0896, 
    //   lng: 72.8656 
    // },
    // { 
    //   name: 'Mumbai Airport Terminal 2', 
    //   address: 'Chhatrapati Shivaji International Airport, Andheri East, Mumbai', 
    //   lat: 19.0896, 
    //   lng: 72.8656 
    // },
    // // { 
    //   name: 'Chhatrapati Shivaji Maharaj Terminus', 
    //   address: 'CST Railway Station, Fort, Mumbai', 
    //   lat: 18.9398, 
    //   lng: 72.8355 
    // },
    // { 
    //   name: 'Bandra Kurla Complex', 
    //   address: 'BKC Business District, Bandra East, Mumbai', 
    //   lat: 19.0728, 
    //   lng: 72.8826 
    // },
    // { 
    //   name: 'Powai Lake', 
    //   address: 'Powai, Hiranandani Gardens, Mumbai', 
    //   lat: 19.1197, 
    //   lng: 72.9073 
    // },
    // { 
    //   name: 'Gateway of India', 
    //   address: 'Apollo Bandar, Colaba, Mumbai', 
    //   lat: 18.9220, 
    //   lng: 72.8347 
    // },
    // { 
    //   name: 'Marine Drive', 
    //   address: 'Queen\'s Necklace, Marine Drive, Mumbai', 
    //   lat: 18.9467, 
    //   lng: 72.8238 
    // },
    // { 
    //   name: 'Juhu Beach', 
    //   address: 'Juhu Tara Road, Juhu, Mumbai', 
    //   lat: 19.0990, 
    //   lng: 72.8269 
    // }
  ];

  constructor(private rideService: RideService) {}

  ngOnInit(): void {
    this.initializeComponent();
  }

  /**
   * Initialize component with default values
   */
  private initializeComponent(): void {
    // Optionally set current location as pickup
    this.pickupLocation = 'Current Location (Andheri West)';
    
    // Initialize date/time for scheduling
    const now = new Date();
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    this.selectedDate = tomorrow.toISOString().split('T')[0];
    this.selectedTime = '09:00';
  }

  /**
   * Handle location suggestion selection
   */
  selectLocationSuggestion(suggestion: LocationSuggestion, type: 'pickup' | 'destination'): void {
    if (type === 'pickup') {
      this.pickupLocation = suggestion.name;
      this.pickupCoords = { lat: suggestion.lat, lng: suggestion.lng };
    } else {
      this.destinationLocation = suggestion.name;
      this.destinationCoords = { lat: suggestion.lat, lng: suggestion.lng };
    }
  }

  /**
   * Toggle scheduled ride option
   */
  toggleScheduled(): void {
    this.isScheduled = !this.isScheduled;
    if (!this.isScheduled) {
      this.selectedDate = '';
      this.selectedTime = '';
    } else {
      // Set default values when scheduling is enabled
      const now = new Date();
      const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
      this.selectedDate = tomorrow.toISOString().split('T')[0];
      this.selectedTime = '09:00';
    }
  }

  /**
   * Proceed from location step to vehicle selection
   */
  proceedToVehicleSelection(): void {
    if (!this.validateLocationInputs()) {
      return;
    }

    this.isLoading = true;

    // Get pickup coordinates
    this.geocodeLocation(this.pickupLocation)
      .then(pickupCoords => {
        this.pickupCoords = pickupCoords;
        
        // Get destination coordinates
        return this.geocodeLocation(this.destinationLocation);
      })
      .then(destinationCoords => {
        this.destinationCoords = destinationCoords;
        
        // Calculate distance
        return this.calculateDistance(this.pickupCoords!, this.destinationCoords!);
      })
      .then(distance => {
        this.setTripDistance(distance);
        
        // Load available vehicles
        return this.loadAvailableVehicles();
      })
      .then(() => {
        this.currentStep = 'vehicle';
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error in location processing:', error);
        this.handleError('Failed to process locations. Please try again.');
      });
  }

  /**
   * Validate location inputs
   */
  private validateLocationInputs(): boolean {
    if (!this.pickupLocation.trim()) {
      this.showError('Please enter pickup location.');
      return false;
    }

    if (!this.destinationLocation.trim()) {
      this.showError('Please enter destination location.');
      return false;
    }

    if (this.isScheduled) {
      if (!this.selectedDate) {
        this.showError('Please select a date for scheduled ride.');
        return false;
      }

      if (!this.selectedTime) {
        this.showError('Please select a time for scheduled ride.');
        return false;
      }

      // Validate that scheduled time is in the future
      const scheduledDateTime = new Date(`${this.selectedDate}T${this.selectedTime}`);
      if (scheduledDateTime <= new Date()) {
        this.showError('Scheduled time must be in the future.');
        return false;
      }
    }

    return true;
  }

  /**
   * Geocode location using service
   */
  private geocodeLocation(location: string): Promise<{ lat: number, lng: number }> {
    return new Promise((resolve, reject) => {
      this.rideService.geocodeAddress(location).subscribe({
        next: (coords) => resolve(coords),
        error: (error) => reject(error)
      });
    });
  }

  /**
   * Calculate distance between two points
   */
  private calculateDistance(pickup: { lat: number, lng: number }, destination: { lat: number, lng: number }): Promise<number> {
    return new Promise((resolve, reject) => {
      this.rideService.getDistance(pickup.lat, pickup.lng, destination.lat, destination.lng).subscribe({
        next: (distance) => resolve(distance),
        error: (error) => reject(error)
      });
    });
  }

  /**
   * Set trip distance and duration estimates
   */
  private setTripDistance(distanceKm: number): void {
    this.estimatedDistanceKm = parseFloat(distanceKm.toFixed(1));
    this.estimatedDistanceText = `${this.estimatedDistanceKm} km`;
    
    // Calculate estimated duration (simple heuristic)
    const baseTime = 10; // Base time in minutes
    const timePerKm = 2.5; // Average time per km in city traffic
    const estimatedMinutes = Math.floor(baseTime + (this.estimatedDistanceKm * timePerKm));
    
    // Add range for traffic variability
    const minTime = estimatedMinutes;
    const maxTime = Math.floor(estimatedMinutes * 1.5);
    this.estimatedDurationText = `${minTime}-${maxTime} min`;
  }

  /**
   * Load available vehicles from service
   */
  private loadAvailableVehicles(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.rideService.listCabs().subscribe({
        next: (cabs) => {
          // Normalize response to array
          this.allCabs = Array.isArray(cabs) ? cabs : [];
          
          // Filter available vehicles
          this.availableCabs = this.allCabs.filter(cab => cab.isAvailable);
          
          // Pre-select first available vehicle
          this.selectedCab = this.availableCabs.length > 0 ? this.availableCabs[0] : null;
          
          // Calculate initial fare estimate
          this.calculateFareEstimate();
          
          resolve();
        },
        error: (error) => {
          console.error('Error loading vehicles:', error);
          reject(error);
        }
      });
    });
  }

  /**
   * Select a vehicle
   */
  selectCab(cab: Cab): void {
    if (!cab.isAvailable) {
      this.showError('This vehicle is currently unavailable.');
      return;
    }

    this.selectedCab = cab;
    this.calculateFareEstimate();
  }

  /**
   * Calculate fare estimate based on selected vehicle
   */
  private calculateFareEstimate(): void {
    if (!this.selectedCab) {
      this.estimatedFare = 0;
      return;
    }

    // Calculate fare: base fare + (distance × rate per km)
    const distanceCost = this.estimatedDistanceKm * this.selectedCab.perKmRate;
    this.estimatedFare = Math.round(this.baseFare + distanceCost);
  }

  /**
   * Proceed from vehicle step to confirmation
   */
  proceedToConfirmation(): void {
    if (!this.selectedCab) {
      this.showError('Please select a vehicle to continue.');
      return;
    }

    this.currentStep = 'confirmation';
  }

  /**
   * Confirm and submit booking
   */
  confirmBooking(): void {
    if (!this.validateBookingData()) {
      return;
    }

    this.isLoading = true;

    try {
      const customerId = this.getCurrentCustomerId();
      const bookingPayload = this.createBookingPayload(customerId);

      this.rideService.bookRide(bookingPayload).subscribe({
        next: (response: BookRideResponse) => {
          this.handleBookingSuccess(response);
        },
        error: (error) => {
          console.error('Booking error:', error);
          this.handleError('Failed to book ride. Please try again.');
        }
      });

    } catch (error) {
      console.error('Booking validation error:', error);
      this.handleError('Please log in to book a ride.');
    }
  }

  /**
   * Validate booking data before submission
   */
  private validateBookingData(): boolean {
    if (!this.pickupCoords || !this.destinationCoords) {
      this.showError('Location coordinates are missing. Please go back and reselect locations.');
      return false;
    }

    if (!this.selectedCab) {
      this.showError('Please select a vehicle.');
      return false;
    }

    if (this.estimatedDistanceKm <= 0) {
      this.showError('Invalid trip distance. Please recalculate.');
      return false;
    }

    return true;
  }

  /**
   * Create booking request payload
   */
  private createBookingPayload(customerId: number): BookRideRequest {
    let scheduledTime: string | null = null;

    if (this.isScheduled && this.selectedDate && this.selectedTime) {
      scheduledTime = `${this.selectedDate}T${this.selectedTime}:00`;
    }

    return {
      customerId: customerId,
      fromLocation: this.pickupLocation,
      toLocation: this.destinationLocation,
      distanceInKm: this.estimatedDistanceKm,
      carType: this.selectedCab!.carType,
      fromLatitude: this.pickupCoords!.lat,
      fromLongitude: this.pickupCoords!.lng,
      scheduledTime: scheduledTime
    };
  }

  /**
   * Handle successful booking
   */
  private handleBookingSuccess(response: BookRideResponse): void {
    this.isLoading = false;
    
    // Show success message with booking details
    const message = `🎉 Ride booked successfully!\n\nBooking ID: ${response.tripBookingId}\n\nYour driver will arrive shortly at the pickup location.`;
    alert(message);
    
    // Reset form for new booking
    this.resetBookingForm();
  }

  /**
   * Get current customer ID from authentication
   */
  private getCurrentCustomerId(): number {
    const userStr = localStorage.getItem('currentUser');
    
    if (!userStr) {
      throw new Error('User not authenticated');
    }

    try {
      const user = JSON.parse(userStr);
      
      if (!user?.id) {
        throw new Error('Invalid user data');
      }

      return user.id;
    } catch (error) {
      throw new Error('Failed to parse user data');
    }
  }

  /**
   * Navigate back to previous step
   */
  goBack(): void {
    switch (this.currentStep) {
      case 'vehicle':
        this.currentStep = 'location';
        break;
      case 'confirmation':
        this.currentStep = 'vehicle';
        break;
      default:
        // Already at first step
        break;
    }
  }

  /**
   * Reset booking form to initial state
   */
  resetBookingForm(): void {
    // Reset step
    this.currentStep = 'location';
    
    // Reset form inputs
    this.pickupLocation = '';
    this.destinationLocation = '';
    this.isScheduled = false;
    this.selectedDate = '';
    this.selectedTime = '';
    
    // Reset coordinates and trip data
    this.pickupCoords = null;
    this.destinationCoords = null;
    this.estimatedDistanceKm = 0;
    this.estimatedDistanceText = '';
    this.estimatedDurationText = '';
    
    // Reset vehicle data
    this.allCabs = [];
    this.availableCabs = [];
    this.selectedCab = null;
    this.estimatedFare = 0;
    
    // Reset loading state
    this.isLoading = false;

    // Re-initialize component
    this.initializeComponent();
  }

  /**
   * Format currency for display
   */
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }

  /**
   * Show error message to user
   */
  private showError(message: string): void {
    alert(message);
  }

  /**
   * Handle errors and reset loading state
   */
  private handleError(message: string): void {
    this.isLoading = false;
    this.showError(message);
  }

  /**
   * Check if form is valid for current step
   */
  isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 'location':
        return !!(this.pickupLocation.trim() && this.destinationLocation.trim());
      case 'vehicle':
        return !!this.selectedCab;
      case 'confirmation':
        return true;
      default:
        return false;
    }
  }

  /**
   * Get step completion status
   */
  getStepStatus(step: string): 'active' | 'completed' | 'pending' {
    const steps = ['location', 'vehicle', 'confirmation'];
    const currentIndex = steps.indexOf(this.currentStep);
    const stepIndex = steps.indexOf(step);

    if (stepIndex < currentIndex) {
      return 'completed';
    } else if (stepIndex === currentIndex) {
      return 'active';
    } else {
      return 'pending';
    }
  }

  /**
   * Filter location suggestions based on input
   */
  getFilteredSuggestions(input: string): LocationSuggestion[] {
    if (!input || input.length < 2) {
      return [];
    }

    const searchTerm = input.toLowerCase().trim();
    return this.locationSuggestions.filter(suggestion =>
      suggestion.name.toLowerCase().includes(searchTerm) ||
      suggestion.address.toLowerCase().includes(searchTerm)
    ).slice(0, 5); // Limit to top 5 results
  }

  /**
   * Handle input changes for location fields
   */
  onLocationInput(value: string, type: 'pickup' | 'destination'): void {
    if (type === 'pickup') {
      this.pickupLocation = value;
      this.pickupCoords = null; // Reset coordinates when manually typing
    } else {
      this.destinationLocation = value;
      this.destinationCoords = null; // Reset coordinates when manually typing
    }
  }

  /**
   * Calculate estimated arrival time for scheduled rides
   */
  getEstimatedArrivalTime(): string {
    if (!this.isScheduled || !this.selectedDate || !this.selectedTime) {
      return 'Now';
    }

    const scheduledDateTime = new Date(`${this.selectedDate}T${this.selectedTime}`);
    return scheduledDateTime.toLocaleString('en-IN', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Get vehicle icon based on car type
   */
  getVehicleIcon(carType: string): string {
    const iconMap: { [key: string]: string } = {
      'economy': '🚗',
      'comfort': '🚙',
      'premium': '🚘',
      'luxury': '🏎️',
      'suv': '🚙',
      'sedan': '🚗'
    };

    return iconMap[carType.toLowerCase()] || '🚗';
  }

  /**
   * Calculate carbon footprint estimate
   */
  getCarbonFootprint(): number {
    // Average CO2 emission per km (in grams)
    const avgEmissionPerKm = 120;
    return Math.round(this.estimatedDistanceKm * avgEmissionPerKm);
  }
}
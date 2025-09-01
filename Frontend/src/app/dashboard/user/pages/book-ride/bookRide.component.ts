import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { RideService, BookRideRequest, BookRideResponse, Driver as ApiDriver, Cab } from '../../services/ride.service';
import { RideService ,BookRideRequest,BookRideResponse,Driver as ApiDriver,Cab} from '../../../../core/services/user/ride.service';

interface Location {
  name: string;
  address: string;
  lat: number;
  lng: number;
}

interface VehicleType {
  id: string;
  name: string;
  capacity: number;
  baseFare: number;
  perKmRate: number;
  icon: string;
  estimatedTime: string;
  features: string[];
}

interface Driver {
  id: string;
  name: string;
  rating: number;
  vehicleNumber: string;
  vehicleType: string;
  estimatedArrival: string;
  photo: string;
  carModel: string;
  licensePlate: string;
  eta: string;
  fare: number;
  apiDriver?: ApiDriver; // Reference to the actual API driver object
  // apiCab?: Cab; // Reference to the actual API cab object
}

@Component({
  selector: 'app-book-ride',
  standalone: false,
  templateUrl: './bookRide.component.html',
  styleUrls: ['./bookRide.component.css']
})
export class BookRideComponent implements OnInit {
  // Properties matching the template
  pickup: string = '';
  destination: string = '';
  selectedDate: string = '';
  selectedTime: string = '';
  
  // Booking form data
  pickupLocation: string = '';
  destinationLocation: string = '';
  selectedVehicleType: string = '';
  scheduledTime: string = '';
  isScheduled: boolean = false;
  rideNotes: string = '';

  // Component state
  currentStep: 'location' | 'vehicle' | 'confirmation' = 'location';
  isLoading: boolean = false;
  availableDrivers: Driver[] = [];
  estimatedFare: number = 0;
  estimatedDistance: string = '';
  estimatedDuration: string = '';
  
  // Properties required by template
  searchingDrivers: boolean = false;
  showDrivers: boolean = false;

  // Location suggestions
  locationSuggestions: Location[] = [
    { name: 'Mumbai Airport T1', address: 'Terminal 1, Andheri East, Mumbai', lat: 19.0896, lng: 72.8656 },
    { name: 'Mumbai Airport T2', address: 'Terminal 2, Andheri East, Mumbai', lat: 19.0896, lng: 72.8656 },
    { name: 'Chhatrapati Shivaji Terminus', address: 'CST, Fort, Mumbai', lat: 18.9398, lng: 72.8355 },
    { name: 'Bandra Kurla Complex', address: 'BKC, Bandra East, Mumbai', lat: 19.0728, lng: 72.8826 },
    { name: 'Powai Lake', address: 'Powai, Mumbai', lat: 19.1197, lng: 72.9073 },
    { name: 'Gateway of India', address: 'Apollo Bandar, Colaba, Mumbai', lat: 18.9220, lng: 72.8347 }
  ];

  // Vehicle types
  vehicleTypes: VehicleType[] = [
    {
      id: 'Sedan',
      name: 'Economy',
      capacity: 4,
      baseFare: 50,
      perKmRate: 8,
      icon: '🚗',
      estimatedTime: '5-8 min',
      features: ['Air Conditioning', 'Music System']
    },
    {
      id: 'Premium',
      name: 'Comfort',
      capacity: 4,
      baseFare: 80,
      perKmRate: 12,
      icon: '🚙',
      estimatedTime: '3-6 min',
      features: ['Premium Interior', 'WiFi', 'Phone Charger']
    },
    {
      id: 'Luxury',
      name: 'Luxury',
      capacity: 4,
      baseFare: 150,
      perKmRate: 20,
      icon: '🚕',
      estimatedTime: '2-5 min',
      features: ['Leather Seats', 'Premium Sound', 'Complimentary Water']
    },
    {
      id: 'SUV',
      name: 'SUV',
      capacity: 6,
      baseFare: 100,
      perKmRate: 15,
      icon: '🚐',
      estimatedTime: '4-7 min',
      features: ['Spacious', 'Large Boot Space', 'Group Travel']
    }
  ];

  // Store the selected driver for booking
  private selectedDriver: Driver | null = null;

  constructor(private rideService: RideService) {}

  ngOnInit(): void {
    this.setCurrentLocationAsPickup();
  }

  setCurrentLocationAsPickup(): void {
    // Simulate getting current location
    this.pickup = 'Current Location (Andheri West)';
    this.pickupLocation = this.pickup;
  }

  // Methods required by template
  searchDrivers(): void {
    if (!this.pickup.trim() || !this.destination.trim()) {
      alert('Please enter both pickup and destination locations.');
      return;
    }

    this.searchingDrivers = true;
    this.showDrivers = false;

    // Sync with existing properties
    this.pickupLocation = this.pickup;
    this.destinationLocation = this.destination;

    // Get available drivers from API
    this.rideService.getAvailableDrivers(this.pickupLocation, this.destinationLocation)
      .subscribe({
        next: (apiDrivers) => {
          this.transformApiDriversToUiDrivers(apiDrivers);
          this.searchingDrivers = false;
          this.showDrivers = true;
        },
        error: (error) => {
          console.error('Error fetching drivers:', error);
          alert('Failed to find available drivers. Please try again.');
          this.searchingDrivers = false;
        }
      });
  }

  // Transform API drivers to UI drivers
  private transformApiDriversToUiDrivers(apiDrivers: ApiDriver[]): void {
    this.availableDrivers = apiDrivers.map((apiDriver, index) => {
      // In a real app, you would get this from the API or match with available cabs
      const vehicleType = this.vehicleTypes[index % this.vehicleTypes.length];
      const distance = parseFloat(this.estimatedDistance) || 10; // Default to 10km if not calculated
      const fare = this.rideService.calculateFare(distance, vehicleType.perKmRate, vehicleType.baseFare);
      
      return {
        id: `D${apiDriver.id}`,
        name: apiDriver.username,
        rating: apiDriver.rating,
        vehicleNumber: apiDriver.licenceNo,
        vehicleType: vehicleType.id,
        estimatedArrival: `${5 + index} min`,
        photo: '👨‍💼', // Default emoji
        carModel: vehicleType.name,
        licensePlate: apiDriver.licenceNo,
        eta: `${5 + index} min`,
        fare: fare,
        apiDriver: apiDriver
      };
    });
  }

  callDriver(driver: Driver): void {
    alert(`Calling ${driver.name} at ${driver.vehicleNumber}`);
  }

  messageDriver(driver: Driver): void {
    alert(`Messaging ${driver.name}`);
  }

  bookRide(driver: Driver): void {
    this.selectedDriver = driver;
    const confirmation = confirm(`Book ride with ${driver.name} for ${this.formatCurrency(driver.fare)}?`);
    
    if (confirmation) {
      this.confirmBookingWithDriver();
    }
  }

  // Actually book the ride with the selected driver
  private confirmBookingWithDriver(): void {
    if (!this.selectedDriver) {
      alert('No driver selected');
      return;
    }

    this.isLoading = true;
    
    // Prepare the request payload
    const request: BookRideRequest = {
      customerId: this.getCurrentCustomerId(), // You need to implement this based on your auth system
      fromLocation: this.pickupLocation,
      toLocation: this.destinationLocation,
      distanceInKm: parseFloat(this.estimatedDistance) || 10, // Default to 10km if not calculated
      carType: this.selectedDriver.vehicleType,
      scheduledTime: this.isScheduled && this.scheduledTime ? this.scheduledTime : null
    };

    // Call the API to book the ride
    this.rideService.bookRide(request).subscribe({
      next: (response: BookRideResponse) => {
        this.isLoading = false;
        alert(`Ride booked successfully with ${response.driver.username}! Your booking ID is ${response.tripBookingId}.`);
        this.resetBooking();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error booking ride:', error);
        alert('Failed to book ride. Please try again.');
      }
    });
  }

  // Get current customer ID from authentication (you'll need to implement based on your auth system)
  private getCurrentCustomerId(): number {
    // This is a placeholder - you should replace with actual authentication logic
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      const user = JSON.parse(userData);
      return user.id;
    }
    
    // Fallback - in a real app, you would redirect to login
    alert('Please log in to book a ride');
    throw new Error('User not authenticated');
  }

  selectLocationSuggestion(location: Location, type: 'pickup' | 'destination'): void {
    if (type === 'pickup') {
      this.pickup = location.name;
      this.pickupLocation = location.name;
    } else {
      this.destination = location.name;
      this.destinationLocation = location.name;
    }
  }

  proceedToVehicleSelection(): void {
    if (!this.pickupLocation.trim() || !this.destinationLocation.trim()) {
      alert('Please enter both pickup and destination locations.');
      return;
    }

    this.currentStep = 'vehicle';
    this.calculateEstimates();
  }

  calculateEstimates(): void {
    // Simulate distance calculation based on locations
    const baseDistance = Math.random() * 20 + 5; // 5-25 km
    this.estimatedDistance = `${baseDistance.toFixed(1)} km`;
    this.estimatedDuration = `${Math.floor(baseDistance * 2 + 10)}-${Math.floor(baseDistance * 3 + 15)} min`;
  }

  selectVehicleType(vehicleType: VehicleType): void {
    this.selectedVehicleType = vehicleType.id;
    
    // Calculate fare based on vehicle type
    const distance = parseFloat(this.estimatedDistance);
    this.estimatedFare = vehicleType.baseFare + (distance * vehicleType.perKmRate);
    
    this.findAvailableDrivers(vehicleType.id);
  }
calculateVehicleFare(vehicle: VehicleType): number {
  const distance = parseFloat(this.estimatedDistance) || 0;
  return vehicle.baseFare + (distance * vehicle.perKmRate);
}

  findAvailableDrivers(vehicleTypeId: string): void {
    this.isLoading = true;
    
    // Get available drivers for the selected vehicle type
    this.rideService.getAvailableDrivers(this.pickupLocation, this.destinationLocation)
      .subscribe({
        next: (apiDrivers) => {
          // Filter drivers by vehicle type and transform to UI format
          this.availableDrivers = apiDrivers
            .filter((driver, index) => {
              // In a real app, you would check the driver's actual vehicle type
              // This is a simplified filter for demonstration
              const availableTypes = ['Sedan', 'Premium', 'Luxury', 'SUV'];
              const driverType = availableTypes[index % availableTypes.length];
              return driverType === vehicleTypeId;
            })
            .map((apiDriver, index) => {
              const vehicleType = this.getVehicleTypeById(vehicleTypeId);
              const distance = parseFloat(this.estimatedDistance);
              const fare = vehicleType ? 
                vehicleType.baseFare + (distance * vehicleType.perKmRate) : 
                0;
              
              return {
                id: `D${apiDriver.id}`,
                name: apiDriver.username,
                rating: apiDriver.rating,
                vehicleNumber: apiDriver.licenceNo,
                vehicleType: vehicleTypeId,
                estimatedArrival: `${5 + index} min`,
                photo: '👨‍💼',
                carModel: vehicleType?.name || 'Car',
                licensePlate: apiDriver.licenceNo,
                eta: `${5 + index} min`,
                fare: fare,
                apiDriver: apiDriver
              };
            });
          
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching drivers:', error);
          alert('Failed to find available drivers. Please try again.');
          this.isLoading = false;
        }
      });
  }

  proceedToConfirmation(): void {
    if (!this.selectedVehicleType) {
      alert('Please select a vehicle type.');
      return;
    }
    this.currentStep = 'confirmation';
  }

  confirmBooking(): void {
    this.isLoading = true;
    
    // Prepare the request payload
    const request: BookRideRequest = {
      customerId: this.getCurrentCustomerId(),
      fromLocation: this.pickupLocation,
      toLocation: this.destinationLocation,
      distanceInKm: parseFloat(this.estimatedDistance),
      carType: this.selectedVehicleType,
      scheduledTime: this.isScheduled && this.scheduledTime ? this.scheduledTime : null
    };

    // Call the API to book the ride
    this.rideService.bookRide(request).subscribe({
      next: (response: BookRideResponse) => {
        this.isLoading = false;
        alert(`Ride booked successfully! Your booking ID is ${response.tripBookingId}.`);
        this.resetBooking();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error booking ride:', error);
        alert('Failed to book ride. Please try again.');
      }
    });
  }

  resetBooking(): void {
    this.currentStep = 'location';
    this.pickup = '';
    this.destination = '';
    this.selectedDate = '';
    this.selectedTime = '';
    this.pickupLocation = '';
    this.destinationLocation = '';
    this.selectedVehicleType = '';
    this.scheduledTime = '';
    this.isScheduled = false;
    this.rideNotes = '';
    this.availableDrivers = [];
    this.estimatedFare = 0;
    this.estimatedDistance = '';
    this.estimatedDuration = '';
    this.searchingDrivers = false;
    this.showDrivers = false;
    this.selectedDriver = null;
  }

  goBack(): void {
    if (this.currentStep === 'vehicle') {
      this.currentStep = 'location';
    } else if (this.currentStep === 'confirmation') {
      this.currentStep = 'vehicle';
    }
  }

  swapLocations(): void {
    const temp = this.pickup;
    this.pickup = this.destination;
    this.destination = temp;
    
    // Also swap the existing properties
    const tempLocation = this.pickupLocation;
    this.pickupLocation = this.destinationLocation;
    this.destinationLocation = tempLocation;
  }

  toggleScheduled(): void {
    this.isScheduled = !this.isScheduled;
    if (!this.isScheduled) {
      this.scheduledTime = '';
      this.selectedTime = '';
    }
  }

  getVehicleTypeById(id: string): VehicleType | undefined {
    return this.vehicleTypes.find(v => v.id === id);
  }

  getStarArray(rating: number): boolean[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= Math.floor(rating));
    }
    return stars;
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
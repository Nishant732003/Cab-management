import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
  // Additional properties required by template
  carModel: string;
  licensePlate: string;
  eta: string;
  fare: number;
}

@Component({
  selector: 'app-book-ride',
  standalone: false,
  // imports: [CommonModule, FormsModule],
  templateUrl: './bookRide.component.html',
  styleUrls: ['./bookRide.component.css']
})
export class BookRideComponent implements OnInit {
  // Properties matching the template
  pickup: string = '';
  destination: string = '';
  selectedDate: string = '';
  selectedTime: string = '';
  
  // Booking form data (keeping existing for backward compatibility)
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
      id: 'economy',
      name: 'Economy',
      capacity: 4,
      baseFare: 50,
      perKmRate: 8,
      icon: '🚗',
      estimatedTime: '5-8 min',
      features: ['Air Conditioning', 'Music System']
    },
    {
      id: 'comfort',
      name: 'Comfort',
      capacity: 4,
      baseFare: 80,
      perKmRate: 12,
      icon: '🚙',
      estimatedTime: '3-6 min',
      features: ['Premium Interior', 'WiFi', 'Phone Charger']
    },
    {
      id: 'luxury',
      name: 'Luxury',
      capacity: 4,
      baseFare: 150,
      perKmRate: 20,
      icon: '🚕',
      estimatedTime: '2-5 min',
      features: ['Leather Seats', 'Premium Sound', 'Complimentary Water']
    },
    {
      id: 'suv',
      name: 'SUV',
      capacity: 6,
      baseFare: 100,
      perKmRate: 15,
      icon: '🚐',
      estimatedTime: '4-7 min',
      features: ['Spacious', 'Large Boot Space', 'Group Travel']
    }
  ];

  constructor() {}

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

    // Simulate searching for drivers
    setTimeout(() => {
      this.findAvailableDriversForTemplate();
      this.searchingDrivers = false;
      this.showDrivers = true;
    }, 2000);
  }

  callDriver(driver: Driver): void {
    alert(`Calling ${driver.name} at ${driver.vehicleNumber}`);
  }

  messageDriver(driver: Driver): void {
    alert(`Messaging ${driver.name}`);
  }

  bookRide(driver: Driver): void {
    const confirmation = confirm(`Book ride with ${driver.name} for $${driver.fare}?`);
    if (confirmation) {
      alert(`Ride booked with ${driver.name}! They will arrive in ${driver.eta}.`);
      this.resetBooking();
    }
  }

  findAvailableDriversForTemplate(): void {
    // Create drivers with all required properties for template
    this.availableDrivers = [
      {
        id: 'D001',
        name: 'Rajesh Kumar',
        rating: 4.8,
        vehicleNumber: 'MH 12 AB 1234',
        vehicleType: 'economy',
        estimatedArrival: '3 min',
        photo: '👨‍💼',
        carModel: 'Honda City',
        licensePlate: 'MH 12 AB 1234',
        eta: '3 min',
        fare: 250
      },
      {
        id: 'D002',
        name: 'Suresh Patel',
        rating: 4.6,
        vehicleNumber: 'MH 14 CD 5678',
        vehicleType: 'comfort',
        estimatedArrival: '5 min',
        photo: '👨‍🦲',
        carModel: 'Toyota Camry',
        licensePlate: 'MH 14 CD 5678',
        eta: '5 min',
        fare: 320
      },
      {
        id: 'D003',
        name: 'Amit Singh',
        rating: 4.9,
        vehicleNumber: 'MH 16 EF 9012',
        vehicleType: 'luxury',
        estimatedArrival: '7 min',
        photo: '👨‍⚕️',
        carModel: 'BMW 3 Series',
        licensePlate: 'MH 16 EF 9012',
        eta: '7 min',
        fare: 450
      }
    ];
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
    // Simulate distance and fare calculation
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

  findAvailableDrivers(vehicleTypeId: string): void {
    this.isLoading = true;
    
    // Simulate API call to find drivers
    setTimeout(() => {
      this.availableDrivers = [
        {
          id: 'D001',
          name: 'Rajesh Kumar',
          rating: 4.8,
          vehicleNumber: 'MH 12 AB 1234',
          vehicleType: vehicleTypeId,
          estimatedArrival: '3 min',
          photo: '👨‍💼',
          carModel: 'Honda City',
          licensePlate: 'MH 12 AB 1234',
          eta: '3 min',
          fare: 250
        },
        {
          id: 'D002',
          name: 'Suresh Patel',
          rating: 4.6,
          vehicleNumber: 'MH 14 CD 5678',
          vehicleType: vehicleTypeId,
          estimatedArrival: '5 min',
          photo: '👨‍🦲',
          carModel: 'Toyota Camry',
          licensePlate: 'MH 14 CD 5678',
          eta: '5 min',
          fare: 320
        },
        {
          id: 'D003',
          name: 'Amit Singh',
          rating: 4.9,
          vehicleNumber: 'MH 16 EF 9012',
          vehicleType: vehicleTypeId,
          estimatedArrival: '7 min',
          photo: '👨‍⚕️',
          carModel: 'BMW 3 Series',
          licensePlate: 'MH 16 EF 9012',
          eta: '7 min',
          fare: 450
        }
      ];
      this.isLoading = false;
    }, 1500);
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
    
    // Simulate booking confirmation
    setTimeout(() => {
      this.isLoading = false;
      alert('Ride booked successfully! Driver will arrive in 3-5 minutes.');
      this.resetBooking();
    }, 2000);
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
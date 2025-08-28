import { Component, OnInit } from '@angular/core';

interface Driver {
  id: number;
  name: string;
  rating: number;
  eta: string;
  phone: string;
  carModel: string;
  licensePlate: string;
  photo: string;
  fare: number;
}

@Component({
  selector: 'app-book-ride',
  templateUrl: './book-ride.component.html',
  styleUrls: ['./book-ride.component.css']
})
export class BookRideComponent implements OnInit {
  pickup: string = '';
  destination: string = '';
  selectedDate: string = '';
  selectedTime: string = '';
  availableDrivers: Driver[] = [];
  searchingDrivers: boolean = false;
  showDrivers: boolean = false;

  constructor() {}

  ngOnInit(): void {
    this.setDefaultDateTime();
  }

  setDefaultDateTime(): void {
    const now = new Date();
    this.selectedDate = now.toISOString().split('T')[0];
    this.selectedTime = now.toTimeString().slice(0, 5);
  }

  searchDrivers(): void {
    if (!this.pickup || !this.destination) {
      alert('Please enter both pickup and destination locations');
      return;
    }

    this.searchingDrivers = true;
    this.showDrivers = false;

    // Simulate API call
    setTimeout(() => {
      this.availableDrivers = [
        {
          id: 1,
          name: 'John Smith',
          rating: 4.8,
          eta: '5 mins',
          phone: '+1-555-0101',
          carModel: 'Toyota Camry',
          licensePlate: 'ABC-123',
          photo: '👨‍💼',
          fare: 12.50
        },
        {
          id: 2,
          name: 'Sarah Johnson',
          rating: 4.9,
          eta: '8 mins',
          phone: '+1-555-0102',
          carModel: 'Honda Civic',
          licensePlate: 'XYZ-789',
          photo: '👩‍💼',
          fare: 11.75
        },
        {
          id: 3,
          name: 'Mike Davis',
          rating: 4.7,
          eta: '12 mins',
          phone: '+1-555-0103',
          carModel: 'Ford Focus',
          licensePlate: 'DEF-456',
          photo: '👨‍🦲',
          fare: 13.25
        }
      ];
      this.searchingDrivers = false;
      this.showDrivers = true;
    }, 2000);
  }

  bookRide(driver: Driver): void {
    const confirmed = confirm(`Book ride with ${driver.name}?\nETA: ${driver.eta}\nFare: $${driver.fare}`);
    if (confirmed) {
      alert(`Ride booked successfully with ${driver.name}!`);
      this.resetForm();
    }
  }

  callDriver(driver: Driver): void {
    alert(`Calling ${driver.name} at ${driver.phone}`);
  }

  messageDriver(driver: Driver): void {
    alert(`Opening message to ${driver.name}`);
  }

  resetForm(): void {
    this.pickup = '';
    this.destination = '';
    this.showDrivers = false;
    this.availableDrivers = [];
    this.setDefaultDateTime();
  }

  getStarArray(rating: number): boolean[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating);
    }
    return stars;
  }
}
import { Component, OnInit, OnDestroy } from '@angular/core';

interface Driver {
  id: number;
  name: string;
  rating: number;
  phone: string;
  carModel: string;
  licensePlate: string;
  photo: string;
}

interface CurrentTrip {
  id: number;
  pickup: string;
  destination: string;
  driver: Driver;
  status: 'waiting' | 'en-route' | 'arrived' | 'in-progress' | 'completed';
  fare: number;
  estimatedDuration: string;
  startTime?: string;
  currentLocation?: string;
  progress: number; // 0-100
}

@Component({
  selector: 'app-current-rides',
  templateUrl: './current-rides.component.html',
  styleUrls: ['./current-rides.component.css']
})
export class CurrentRidesComponent implements OnInit, OnDestroy {
  currentTrip: CurrentTrip | null = null;
  trackingActive: boolean = false;
  progressInterval: any;

  constructor() {}

  ngOnInit(): void {
    this.initializeSampleTrip();
    this.startTracking();
  }

  ngOnDestroy(): void {
    if (this.progressInterval) {
      clearInterval(this.progressInterval);
    }
  }

  initializeSampleTrip(): void {
    // Sample current trip data
    this.currentTrip = {
      id: 1001,
      pickup: 'Downtown Shopping Mall',
      destination: 'Airport Terminal 1',
      driver: {
        id: 1,
        name: 'John Smith',
        rating: 4.8,
        phone: '+1-555-0101',
        carModel: 'Toyota Camry',
        licensePlate: 'ABC-123',
        photo: '👨‍💼'
      },
      status: 'en-route',
      fare: 25.50,
      estimatedDuration: '18 mins',
      startTime: '2:30 PM',
      currentLocation: 'Main Street & 5th Ave',
      progress: 35
    };
  }

  startTracking(): void {
    if (!this.currentTrip) return;
    
    this.trackingActive = true;
    
    // Simulate trip progress
    this.progressInterval = setInterval(() => {
      if (this.currentTrip && this.currentTrip.progress < 100) {
        this.currentTrip.progress += Math.random() * 5;
        
        if (this.currentTrip.progress >= 100) {
          this.currentTrip.progress = 100;
          this.currentTrip.status = 'completed';
          this.stopTracking();
        } else {
          this.updateTripStatus();
        }
      }
    }, 3000);
  }

  stopTracking(): void {
    this.trackingActive = false;
    if (this.progressInterval) {
      clearInterval(this.progressInterval);
    }
  }

  updateTripStatus(): void {
    if (!this.currentTrip) return;

    if (this.currentTrip.progress >= 80) {
      this.currentTrip.status = 'in-progress';
      this.currentTrip.currentLocation = 'Approaching destination';
    } else if (this.currentTrip.progress >= 50) {
      this.currentTrip.status = 'in-progress';
      this.currentTrip.currentLocation = 'Highway 101 North';
    } else if (this.currentTrip.progress >= 20) {
      this.currentTrip.status = 'in-progress';
      this.currentTrip.currentLocation = 'City Center';
    }
  }

  callDriver(): void {
    if (this.currentTrip) {
      alert(`Calling ${this.currentTrip.driver.name} at ${this.currentTrip.driver.phone}`);
    }
  }

  messageDriver(): void {
    if (this.currentTrip) {
      alert(`Opening message to ${this.currentTrip.driver.name}`);
    }
  }

  shareLocation(): void {
    alert('Sharing your location with the driver...');
  }

  cancelTrip(): void {
    const confirmed = confirm('Are you sure you want to cancel this trip?');
    if (confirmed) {
      this.currentTrip = null;
      this.stopTracking();
      alert('Trip cancelled successfully');
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'waiting': return '#f39c12';
      case 'en-route': return '#3498db';
      case 'arrived': return '#2ecc71';
      case 'in-progress': return '#27ae60';
      case 'completed': return '#95a5a6';
      default: return '#7f8c8d';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'waiting': return 'Driver Assigned';
      case 'en-route': return 'Driver En Route';
      case 'arrived': return 'Driver Arrived';
      case 'in-progress': return 'Trip in Progress';
      case 'completed': return 'Trip Completed';
      default: return 'Unknown';
    }
  }

  getStarArray(rating: number): boolean[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating);
    }
    return stars;
  }
}
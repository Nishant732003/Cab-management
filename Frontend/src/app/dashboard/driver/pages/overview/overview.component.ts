import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';

import { Driver, Trip, Vehicle, VehicleType, TripStatus, PaymentMethod, Location } from '../../../../core/models/driver/driver.model';

interface TodayStats {
  earnings: number;
  trips: number;
  distance: number;
  earningsChange: number;
  tripsChange: number;
}

interface EarningsSummary {
  gross: number;
  commission: number;
  tips: number;
  net: number;
}

interface ChartData {
  label: string;
  value: number;
}

interface VehicleAlert {
  type: 'warning' | 'error' | 'info';
  message: string;
  id: string; // Add unique identifier for tracking
}

@Component({
  selector: 'app-driver-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css'],
  standalone: false
})
export class OverviewComponent implements OnInit, OnDestroy {

  // Driver data
  driverProfile: Driver | null = null;
  isOnline = false;
  currentTrip: Trip | null = null;

  // Stats
  todayStats: TodayStats = {
    earnings: 245.50,
    trips: 12,
    distance: 89.2,
    earningsChange: 15.2,
    tripsChange: 8.5
  };

  // Recent trips
  recentTrips: Trip[] = [];

  // Earnings
  selectedPeriod = 'today';
  earningsSummary: EarningsSummary = {
    gross: 245.50,
    commission: 49.10,
    tips: 38.75,
    net: 235.15
  };
  earningsChartData: ChartData[] = [];
  maxEarnings = 245;

  // Vehicle
  vehicle: Vehicle | null = null;
  fuelLevel = 75;
  isServiceDue = false;
  vehicleAlerts: VehicleAlert[] = [];

  // Subscriptions
  private subscriptions: Subscription[] = [];

  // Expose Math to template
  Math = Math;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeMockData();
    this.setupAutoRefresh();
    this.checkServiceStatus();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private setupAutoRefresh(): void {
    // Auto-refresh stats every 30 seconds when online
    const refreshSubscription = interval(30000).subscribe(() => {
      if (this.isOnline) {
        this.refreshStats();
      }
    });
    
    this.subscriptions.push(refreshSubscription);
  }

  private refreshStats(): void {
    // Simulate real-time updates
    const randomVariation = (base: number, variance: number) => 
      base + (Math.random() - 0.5) * variance;

    this.todayStats = {
      ...this.todayStats,
      earnings: Math.max(0, randomVariation(this.todayStats.earnings, 10)),
      trips: Math.max(0, Math.floor(randomVariation(this.todayStats.trips, 2))),
      distance: Math.max(0, randomVariation(this.todayStats.distance, 5))
    };

    // Update fuel level simulation
    if (this.isOnline) {
      this.fuelLevel = Math.max(0, this.fuelLevel - Math.random() * 0.5);
    }

    this.cdr.detectChanges();
  }

  private checkServiceStatus(): void {
    const nextServiceDate = this.getNextServiceDate();
    const today = new Date();
    const daysDiff = Math.ceil((nextServiceDate.getTime() - today.getTime()) / (1000 * 3600 * 24));
    
    this.isServiceDue = daysDiff <= 7;
    
    if (this.isServiceDue) {
      this.vehicleAlerts.push({
        id: 'service-due',
        type: 'warning',
        message: `Service due in ${daysDiff} days`
      });
    }
  }

  initializeMockData(): void {
    // Mock driver profile
    this.driverProfile = {
      id: '1',
      name: 'John Doe',
      email: 'john.doe@example.com',
      phone: '+1-555-0123',
      avatar: 'https://via.placeholder.com/150',
      licenseNumber: 'DL123456789',
      rating: 4.8,
      totalTrips: 1247,
      totalEarnings: 45650.75,
      joinDate: new Date('2022-03-15'),
      isOnline: false,
      vehicle: {
        id: '1',
        make: 'Toyota',
        model: 'Camry',
        year: 2020,
        color: 'Silver',
        plateNumber: 'ABC123',
        type: VehicleType.SEDAN,
        fuelType: 'Petrol',
        currentMileage: 45000,
        lastService: new Date('2024-01-15'),
        nextService: new Date('2024-04-15'),
        insurance: {
          provider: 'State Insurance',
          policyNumber: 'POL123456789',
          expiryDate: new Date('2025-12-31'),
          isActive: true
        }
      },
      location: {
        latitude: 28.6139,
        longitude: 77.2090,
        address: '123 Main Street',
        city: 'New Delhi',
        state: 'Delhi'
      }
    };

    this.vehicle = this.driverProfile.vehicle;
    this.isOnline = this.driverProfile.isOnline;

    // Initialize vehicle alerts
    this.vehicleAlerts = [
      { id: 'oil-change', type: 'warning', message: 'Oil change due in 500 miles' },
      { id: 'tire-pressure', type: 'info', message: 'Tire pressure check recommended' }
    ];

    // Mock recent trips
    this.generateMockTrips();

    // Initialize earnings data
    this.loadEarningsData();

    // Check if there's a current trip (mock)
    this.currentTrip = null; // No active trip for now
  }

  private generateMockTrips(): void {
    this.recentTrips = [
      {
        id: '1',
        driverId: '1',
        passengerId: 'p1',
        passengerName: 'Alice Johnson',
        passengerPhone: '+1-555-0001',
        pickupLocation: {
          latitude: 28.6139,
          longitude: 77.2090,
          address: 'Connaught Place',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6500,
          longitude: 77.2300,
          address: 'India Gate',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 2 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 2 * 60 * 60 * 1000 + 15 * 60 * 1000),
        distance: 5.2,
        duration: 15,
        fare: 18.50,
        tip: 2.50,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.UPI,
        rating: 5
      },
      {
        id: '2',
        driverId: '1',
        passengerId: 'p2',
        passengerName: 'Bob Smith',
        passengerPhone: '+1-555-0002',
        pickupLocation: {
          latitude: 28.6000,
          longitude: 77.2100,
          address: 'Karol Bagh',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.6200,
          longitude: 77.2400,
          address: 'Lajpat Nagar',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 4 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 4 * 60 * 60 * 1000 + 22 * 60 * 1000),
        distance: 7.8,
        duration: 22,
        fare: 24.75,
        tip: 5.00,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CARD,
        rating: 4
      },
      {
        id: '3',
        driverId: '1',
        passengerId: 'p3',
        passengerName: 'Carol White',
        passengerPhone: '+1-555-0003',
        pickupLocation: {
          latitude: 28.5800,
          longitude: 77.1900,
          address: 'Rajouri Garden',
          city: 'New Delhi',
          state: 'Delhi'
        },
        dropLocation: {
          latitude: 28.7000,
          longitude: 77.2500,
          address: 'Rohini',
          city: 'New Delhi',
          state: 'Delhi'
        },
        startTime: new Date(Date.now() - 6 * 60 * 60 * 1000),
        endTime: new Date(Date.now() - 6 * 60 * 60 * 1000 + 28 * 60 * 1000),
        distance: 12.1,
        duration: 28,
        fare: 31.25,
        tip: 3.75,
        status: TripStatus.COMPLETED,
        paymentMethod: PaymentMethod.CASH,
        rating: 5
      }
    ];
  }

  toggleOnlineStatus(): void {
    this.isOnline = !this.isOnline;
    if (this.driverProfile) {
      this.driverProfile.isOnline = this.isOnline;
    }
    
    // Simulate API call feedback
    console.log(`Driver is now ${this.isOnline ? 'online' : 'offline'}`);
    
    // Show success message (you can integrate with a toast service)
    this.showStatusChangeNotification();
  }

  private showStatusChangeNotification(): void {
    // This would typically integrate with a notification service
    console.log(`Status changed to ${this.isOnline ? 'Online' : 'Offline'}`);
  }

  loadEarningsData(): void {
    this.onPeriodChange(this.selectedPeriod);
  }

  onPeriodChange(period: string): void {
    this.selectedPeriod = period;
    
    // Mock different data based on period
    switch (period) {
      case 'today':
        this.earningsSummary = {
          gross: 245.50,
          commission: 49.10,
          tips: 38.75,
          net: 235.15
        };
        this.earningsChartData = [
          { label: '6AM', value: 25 },
          { label: '9AM', value: 45 },
          { label: '12PM', value: 65 },
          { label: '3PM', value: 40 },
          { label: '6PM', value: 80 },
          { label: '9PM', value: 55 }
        ];
        break;
      case 'week':
        this.earningsSummary = {
          gross: 1520.75,
          commission: 304.15,
          tips: 245.30,
          net: 1461.90
        };
        this.earningsChartData = [
          { label: 'Mon', value: 125 },
          { label: 'Tue', value: 180 },
          { label: 'Wed', value: 95 },
          { label: 'Thu', value: 220 },
          { label: 'Fri', value: 245 },
          { label: 'Sat', value: 185 },
          { label: 'Sun', value: 165 }
        ];
        break;
      case 'month':
        this.earningsSummary = {
          gross: 6842.25,
          commission: 1368.45,
          tips: 1095.60,
          net: 6569.40
        };
        this.earningsChartData = [
          { label: 'Week 1', value: 1520 },
          { label: 'Week 2', value: 1680 },
          { label: 'Week 3', value: 1895 },
          { label: 'Week 4', value: 1747 }
        ];
        break;
    }
    
    this.maxEarnings = Math.max(...this.earningsChartData.map(item => item.value));
  }

  formatTripTime(timestamp: Date): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const hours = Math.floor(diff / (1000 * 60 * 60));
    
    if (hours < 1) {
      const minutes = Math.floor(diff / (1000 * 60));
      return `${minutes}m ago`;
    } else if (hours < 24) {
      return `${hours}h ago`;
    } else {
      const days = Math.floor(hours / 24);
      return `${days}d ago`;
    }
  }

  getNextServiceDate(): Date {
    return this.vehicle?.nextService || new Date();
  }

  // Quick action methods
  viewTripHistory(): void {
    this.router.navigate(['/driver/trips']);
  }

  viewEarnings(): void {
    this.router.navigate(['/driver/earnings']);
  }

  reportIssue(): void {
    console.log('Report issue clicked');
    // In a real app, this would open a modal or navigate to issue reporting
    alert('Issue reporting feature will be available soon!');
  }

  contactSupport(): void {
    console.log('Contact support clicked');
    // In a real app, this would open a support chat or call system
    alert('Support contact feature will be available soon!');
  }

  // TrackBy functions for performance optimization
  trackByTripId(index: number, trip: Trip): string {
    return trip.id;
  }

  trackByChartLabel(index: number, item: ChartData): string {
    return item.label;
  }

  trackByAlertMessage(index: number, alert: VehicleAlert): string {
    return alert.id;
  }
}
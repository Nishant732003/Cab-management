import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { Trip, DriverService } from '../../../../core/services/driver/driver.service';
import { Driver } from '../../../../core/models/driver/driver.model';

// Interface definitions to ensure type safety
interface TodayStats {
  earnings: number;
  trips: number;
  distanceinKm: number;
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
  id: string;
}

@Component({
  selector: 'app-driver-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css'],
  standalone:false
})
export class OverviewComponent implements OnInit, OnDestroy {

  driverProfile: any | null = null;
  isOnline = false;
  currentTrip: Trip | null = null;
  driverId: number = 2; // This should be dynamically retrieved from an authentication service

  todayStats: TodayStats = {
    earnings: 0,
    trips: 0,
    distanceinKm: 0
  };

  recentTrips: Trip[] = [];

  selectedPeriod = 'today';
  earningsSummary: EarningsSummary = { gross: 0, commission: 0, tips: 0, net: 0 };
  earningsChartData: ChartData[] = [];
  maxEarnings = 0;

  vehicle: any | null = null;
  fuelLevel = 75; // Mock data, should be from an API if available
  isServiceDue = false;
  vehicleAlerts: VehicleAlert[] = [];

  private subscriptions: Subscription[] = [];
  Math = Math;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private driverService: DriverService
  ) {}

  ngOnInit(): void {
    this.loadDriverData();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private loadDriverData(): void {
    // Assuming a way to get the profile and trips for the same driverId
    this.driverService.getDriverProfile('Raj').subscribe(profile => {
      this.driverProfile = profile;
      this.vehicle = profile.cab;
      this.isOnline = profile.isAvailable;
      this.cdr.detectChanges();
    });

    this.driverService.getTripsForDriver(this.driverId).subscribe(trips => {
      this.recentTrips = trips.sort((a, b) => new Date(b.fromDateTime).getTime() - new Date(a.fromDateTime).getTime());
      this.calculateTodayStats(this.recentTrips);
      this.loadEarningsData();
      this.cdr.detectChanges();
    });
  }

  private calculateTodayStats(trips: Trip[]): void {
    const today = new Date();
    // Filter trips that were completed today
    const todayTrips = trips.filter((trip:any) =>
      trip.status === 'COMPLETED' && new Date(trip.toDateTime).toDateString() === today.toDateString()
    );

    this.todayStats.earnings = todayTrips.reduce((sum, trip) => sum + trip.bill, 0);
    this.todayStats.trips = todayTrips.length;
    this.todayStats.distanceinKm = todayTrips.reduce((sum:any, trip:any) => sum + trip.distanceinKm, 0);
  }

  toggleOnlineStatus(): void {
    this.isOnline = !this.isOnline;
    if (this.driverProfile) {
      this.driverProfile.isAvailable = this.isOnline;
      this.cdr.detectChanges();
    }
  }

  private setupAutoRefresh(): void {
    // Refresh stats every minute
    const refreshSubscription = interval(60000).subscribe(() => {
      this.loadDriverData();
    });
    this.subscriptions.push(refreshSubscription);
  }

  loadEarningsData(): void {
    this.onPeriodChange(this.selectedPeriod);
  }

  onPeriodChange(period: string): void {
    switch (period) {
      case 'today':
        this.earningsSummary.gross = this.todayStats.earnings;
        this.earningsSummary.commission = this.todayStats.earnings * 0.2; // Example 20% commission
        this.earningsSummary.tips = 0;
        this.earningsSummary.net = this.earningsSummary.gross - this.earningsSummary.commission + this.earningsSummary.tips;
        this.earningsChartData = [
          { label: 'Today', value: this.todayStats.earnings }
        ];
        break;
      case 'week':
      case 'month':
        // No trip data for week/month is provided, so these are mock values
        this.earningsSummary = { gross: 1520.75, commission: 304.15, tips: 245.30, net: 1461.90 };
        this.earningsChartData = [
          { label: 'Mon', value: 125 }, { label: 'Tue', value: 180 }, { label: 'Wed', value: 95 },
          { label: 'Thu', value: 220 }, { label: 'Fri', value: 245 }, { label: 'Sat', value: 185 },
          { label: 'Sun', value: 165 }
        ];
        break;
    }
    this.maxEarnings = Math.max(...this.earningsChartData.map(item => item.value));
  }

  formatTripTime(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(minutes / 60);

    if (hours < 1) {
      return `${minutes}m ago`;
    } else if (hours < 24) {
      return `${hours}h ago`;
    } else {
      const days = Math.floor(hours / 24);
      return `${days}d ago`;
    }
  }

  getNextServiceDate(): Date {
    // This function provides mock data for next service date since it's not in the provided JSON
    return new Date(new Date().setMonth(new Date().getMonth() + 6));
  }

  viewTripHistory(): void { this.router.navigate(['/driver/trips']); }
  viewEarnings(): void { this.router.navigate(['/driver/earnings']); }
  reportIssue(): void { console.log('Report issue clicked'); }
  contactSupport(): void { console.log('Contact support clicked'); }

  trackByTripId(index: number, trip: Trip): number { return trip.tripBookingId; }
  trackByChartLabel(index: number, item: ChartData): string { return item.label; }
  trackByAlertMessage(index: number, alert: VehicleAlert): string { return alert.id; }
}
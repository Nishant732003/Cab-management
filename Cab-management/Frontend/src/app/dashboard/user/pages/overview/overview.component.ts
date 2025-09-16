import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, forkJoin, of } from 'rxjs';
import { takeUntil, catchError } from 'rxjs/operators';
import { RideService, BookRideResponse } from '../../../../core/services/user/ride.service';

interface Statistics {
  totalRides: number;
  activeDrivers: number;
  totalRevenue: number;
  avgRating: number;
  ridesChange: number;
  driversChange: number;
  revenueChange: number;
  ratingChange: number;
}
interface ChartPeriod { label: string; value: '7d' | '1m' | '3m' | '1y'; }
interface ChartData { label: string; value: number; rides: number; }
interface RideStatus { name: string; count: number; percentage: number; color: string; }
interface RecentRide {
  id: string;
  customerName: string;
  pickup: string;
  destination: string;
  startTime: Date;
  driverName: string;
  status: 'completed' | 'ongoing' | 'pending' | 'cancelled';
  fare: number;
}
interface TopDriver {
  id: string;
  name: string;
  vehicleNumber: string;
  rating: number;
  ridesCompleted: number;
  earnings: number;
  isOnline: boolean;
}
type TripStatus = 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS';
interface Trip {
  tripBookingId: number;
  fromLocation: string;
  toLocation: string;
  fromDateTime: string;
  toDateTime: string | null;
  status: TripStatus;
  distanceInKm: number;
  bill: number;
  customerRating: number | null;
  driver: {
    id: number;
    username: string;
    mobileNumber: string | null;
    rating: number;
    profilePhotoUrl: string | null;
    licenceNo: string;
  };
  cab: {
    cabId: number;
    carType: string;
    numberPlate: string | ' ';
    imageUrl: string | null;
  };
}

@Component({
  selector: 'app-overview',
  standalone: false,
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // State
  isLoading = false;
  selectedTimeFrame: 'today' | 'week' | 'month' | 'year' = 'today';
  selectedChartPeriod: '7d' | '1m' | '3m' | '1y' = '7d';

  // Data containers
  allTrips: Trip[] = [];
  windowTrips: Trip[] = [];

  chartPeriods: ChartPeriod[] = [
    { label: '7D', value: '7d' },
    { label: '1M', value: '1m' },
    { label: '3M', value: '3m' },
    { label: '1Y', value: '1y' }
  ];

  statistics: Statistics = {
    totalRides: 0,
    activeDrivers: 0,
    totalRevenue: 0,
    avgRating: 0,
    ridesChange: 0,
    driversChange: 0,
    revenueChange: 0,
    ratingChange: 0
  };

  chartData: ChartData[] = [];
  rideStatusData: RideStatus[] = [];
  recentRides: RecentRide[] = [];
  topDrivers: TopDriver[] = [];

  constructor(private router: Router, private rideService: RideService) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.startDataRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Utilities reused from TripHistory
  private getCurrentUserId(): number | null {
    try {
      const raw = localStorage.getItem('currentUser');
      if (!raw) return null;
      const parsed = JSON.parse(raw);
      return typeof parsed?.id === 'number' ? parsed.id : null;
    } catch {
      return null;
    }
  }

  private normalizeStatus(s: string): TripStatus {
    switch ((s || '').toUpperCase()) {
      case 'COMPLETED': return 'COMPLETED';
      case 'CANCELLED': return 'CANCELLED';
      case 'IN_PROGRESS': return 'IN_PROGRESS';
      case 'CONFIRMED': return 'CONFIRMED';
      case 'SCHEDULED': return 'CONFIRMED';
      default: return 'CONFIRMED';
    }
  }

  private mapApiResponseToTrip(api: any): Trip {
    const safeDriver = {
      id: api.driverId || 0,
      username: `${api.driverFirstName || 'Driver'} ${api.driverLastName || ''}`.trim() || 'Assigned Soon',
      mobileNumber: null,
      rating: 0,
      profilePhotoUrl: null,
      licenceNo: 'N/A'
    };
    const safeCab = {
      cabId: api.cabId || 0,
      carType: api.carType || 'Vehicle',
      numberPlate: ' ',
      imageUrl: null
    };
    return {
      tripBookingId: api.tripBookingId,
      fromLocation: api.fromLocation,
      toLocation: api.toLocation,
      fromDateTime: api.fromDateTime,
      toDateTime: api.toDateTime ?? null,
      status: this.normalizeStatus(api.status),
      distanceInKm: api.distanceinKm || 0,
      bill: api.bill || 0,
      customerRating: api.customerRating ?? null,
      driver: safeDriver,
      cab: safeCab
    };
  }

  // Data loading
  loadDashboardData(): void {
    this.isLoading = true;
    const customerId = this.getCurrentUserId();
    if (!customerId) {
      this.isLoading = false;
      return;
    }

    this.rideService.getCustomerTrips(customerId).pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Overview load error', err);
        // Fallback to empty dataset; optionally reuse TripHistory mock here
        this.allTrips = [];
        return of([]);
      })
    ).subscribe((trips: BookRideResponse[] | any[]) => {
      this.allTrips = (trips || []).map(t => this.mapApiResponseToTrip(t));
      this.recomputeAll();
      this.isLoading = false;
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }

  startDataRefresh(): void {
    // Optionally keep a ref to clear on destroy
    setInterval(() => this.refreshStatisticsRealtime(), 300000);
  }

  onTimeFrameChange(): void {
    this.recomputeAll();
  }

  setChartPeriod(period: '7d' | '1m' | '3m' | '1y'): void {
    this.selectedChartPeriod = period;
    this.updateChartData();
  }

  // Core recompute pipeline
  private recomputeAll(): void {
    this.windowTrips = this.filterTripsByTimeframe(this.allTrips, this.selectedTimeFrame);
    const compareTrips = this.getComparisonWindowTrips(this.allTrips, this.selectedTimeFrame);

    this.updateStatistics(this.windowTrips, compareTrips);
    this.updateRideStatusData(this.windowTrips);
    this.updateRecentRides(this.windowTrips);
    this.updateTopDrivers(this.windowTrips);
    this.updateChartData();
  }

  // Timeframe helpers
  private filterTripsByTimeframe(trips: Trip[], tf: 'today' | 'week' | 'month' | 'year'): Trip[] {
    const now = new Date();
    const start = new Date(now);
    if (tf === 'today') {
      start.setHours(0, 0, 0, 0);
    } else if (tf === 'week') {
      const day = now.getDay(); // 0 Sun
      const diff = (day === 0 ? 6 : day - 1); // Monday start
      start.setDate(now.getDate() - diff);
      start.setHours(0, 0, 0, 0);
    } else if (tf === 'month') {
      start.setDate(1);
      start.setHours(0, 0, 0, 0);
    } else {
      start.setMonth(0, 1);
      start.setHours(0, 0, 0, 0);
    }
    return trips.filter(t => new Date(t.fromDateTime) >= start && new Date(t.fromDateTime) <= now);
  }

  private getComparisonWindowTrips(trips: Trip[], tf: 'today' | 'week' | 'month' | 'year'): Trip[] {
    const now = new Date();
    let startPrev = new Date();
    let endPrev = new Date();

    if (tf === 'today') {
      // previous day
      startPrev = new Date(now); startPrev.setDate(now.getDate() - 1); startPrev.setHours(0,0,0,0);
      endPrev = new Date(now); endPrev.setDate(now.getDate() - 1); endPrev.setHours(23,59,59,999);
    } else if (tf === 'week') {
      // previous 7 days window ending last week
      endPrev = new Date(now); endPrev.setDate(now.getDate() - (now.getDay() === 0 ? 7 : now.getDay())); // end last Sunday
      startPrev = new Date(endPrev); startPrev.setDate(endPrev.getDate() - 6); startPrev.setHours(0,0,0,0);
      endPrev.setHours(23,59,59,999);
    } else if (tf === 'month') {
      // previous calendar month
      startPrev = new Date(now.getFullYear(), now.getMonth() - 1, 1, 0,0,0,0);
      endPrev = new Date(now.getFullYear(), now.getMonth(), 0, 23,59,59,999);
    } else {
      // previous calendar year
      startPrev = new Date(now.getFullYear() - 1, 0, 1, 0,0,0,0);
      endPrev = new Date(now.getFullYear() - 1, 11, 31, 23,59,59,999);
    }

    return trips.filter(t => {
      const d = new Date(t.fromDateTime);
      return d >= startPrev && d <= endPrev;
    });
  }

  // KPIs
  private updateStatistics(current: Trip[], previous: Trip[]): void {
    const totalRides = current.length;
    const activeDrivers = new Set(current.filter(t => t.status === 'IN_PROGRESS').map(t => t.driver.id)).size;
    const totalRevenue = current.filter(t => t.status === 'COMPLETED').reduce((s, t) => s + (t.bill || 0), 0);
    const ratings = current.map(t => t.customerRating).filter((r): r is number => typeof r === 'number');
    const avgRating = ratings.length ? Number((ratings.reduce((a,b)=>a+b,0) / ratings.length).toFixed(1)) : 0;

    // Changes vs previous window (percent)
    const pct = (cur: number, prev: number) => {
      if (prev === 0) return cur > 0 ? 100 : 0;
      return Number((((cur - prev) / prev) * 100).toFixed(1));
    };
    const prevRides = previous.length;
    const prevActiveDrivers = new Set(previous.filter(t => t.status === 'IN_PROGRESS').map(t => t.driver.id)).size;
    const prevRevenue = previous.filter(t => t.status === 'COMPLETED').reduce((s, t) => s + (t.bill || 0), 0);
    const prevRatings = previous.map(t => t.customerRating).filter((r): r is number => typeof r === 'number');
    const prevAvgRating = prevRatings.length ? (prevRatings.reduce((a,b)=>a+b,0) / prevRatings.length) : 0;

    this.statistics = {
      totalRides,
      activeDrivers,
      totalRevenue,
      avgRating,
      ridesChange: pct(totalRides, prevRides),
      driversChange: pct(activeDrivers, prevActiveDrivers),
      revenueChange: pct(totalRevenue, prevRevenue),
      ratingChange: pct(avgRating, prevAvgRating || 0)
    };
  }

  // Status distribution
  private updateRideStatusData(trips: Trip[]): void {
    const total = Math.max(trips.length, 1);
    const count = (s: TripStatus) => trips.filter(t => t.status === s).length;

    const completed = count('COMPLETED');
    const ongoing = count('IN_PROGRESS');
    const confirmed = count('CONFIRMED');
    const cancelled = count('CANCELLED');

    this.rideStatusData = [
      { name: 'Completed', count: completed, percentage: Number(((completed/total)*100).toFixed(1)), color: '#10b981' },
      { name: 'Ongoing', count: ongoing, percentage: Number(((ongoing/total)*100).toFixed(1)), color: '#3b82f6' },
      { name: 'Pending', count: confirmed, percentage: Number(((confirmed/total)*100).toFixed(1)), color: '#f59e0b' },
      { name: 'Cancelled', count: cancelled, percentage: Number(((cancelled/total)*100).toFixed(1)), color: '#ef4444' }
    ];
  }

  // Recent rides
  private updateRecentRides(trips: Trip[]): void {
    const latest = [...trips].sort((a, b) => new Date(b.fromDateTime).getTime() - new Date(a.fromDateTime).getTime()).slice(0, 5);
    this.recentRides = latest.map(t => ({
      id: `RID${t.tripBookingId}`,
      customerName: this.deriveCustomerName(t) ?? 'Customer',
      pickup: t.fromLocation,
      destination: t.toLocation,
      startTime: new Date(t.fromDateTime),
      driverName: t.driver.username,
      status: this.toUiStatus(t.status),
      fare: t.bill || 0
    }));
  }

  private deriveCustomerName(t: Trip): string | null {
    // If API later provides customer names, map here; else null
    return null;
  }

  private toUiStatus(s: TripStatus): RecentRide['status'] {
    switch (s) {
      case 'COMPLETED': return 'completed';
      case 'IN_PROGRESS': return 'ongoing';
      case 'CANCELLED': return 'cancelled';
      default: return 'pending'; // CONFIRMED
    }
  }

  // Top drivers
  private updateTopDrivers(trips: Trip[]): void {
    const byDriver = new Map<number, {
      name: string; ridesCompleted: number; earnings: number; ratingSum: number; ratingCount: number; isOnline: boolean;
    }>();

    for (const t of trips) {
      const id = t.driver.id || 0;
      if (!byDriver.has(id)) {
        byDriver.set(id, {
          name: t.driver.username || `Driver ${id || ''}`,
          ridesCompleted: 0,
          earnings: 0,
          ratingSum: 0,
          ratingCount: 0,
          isOnline: false
        });
      }
      const agg = byDriver.get(id)!;
      if (t.status === 'COMPLETED') {
        agg.ridesCompleted += 1;
        agg.earnings += t.bill || 0;
      }
      if (typeof t.customerRating === 'number') {
        agg.ratingSum += t.customerRating;
        agg.ratingCount += 1;
      }
      if (t.status === 'IN_PROGRESS') {
        agg.isOnline = true;
      }
    }

    const items = Array.from(byDriver.entries()).map(([id, v]) => ({
      id: id ? String(id) : '0',
      name: v.name,
      vehicleNumber: '', // not in API
      rating: v.ratingCount ? Number((v.ratingSum / v.ratingCount).toFixed(1)) : 0,
      ridesCompleted: v.ridesCompleted,
      earnings: v.earnings,
      isOnline: v.isOnline
    }));

    this.topDrivers = items.sort((a, b) => b.earnings - a.earnings).slice(0, 5);
  }

  // Charts
  private updateChartData(): void {
    const trips = this.windowTrips;
    const groupKey = this.selectedChartPeriod;

    const buckets: { label: string; keyStart: number; rides: number }[] = this.buildChartBuckets(groupKey);
    const byBucket = new Map<number, number>(); // startTimeMs -> rides
    for (const t of trips) {
      const d = new Date(t.fromDateTime);
      const bucketStart = this.bucketStart(d, groupKey).getTime();
      byBucket.set(bucketStart, (byBucket.get(bucketStart) || 0) + 1);
    }

    const filled = buckets.map(b => {
      const rides = byBucket.get(b.keyStart) || 0;
      // value as percentage scale for bar height
      return { label: b.label, value: this.scaleValue(rides, buckets, byBucket), rides };
    });

    this.chartData = filled;
  }

  private buildChartBuckets(period: '7d' | '1m' | '3m' | '1y') {
    const now = new Date();
    const list: { label: string; keyStart: number; rides: number }[] = [];
    if (period === '7d') {
      // last 7 days labels Mon..Sun by date
      for (let i = 6; i >= 0; i--) {
        const d = new Date(now); d.setDate(now.getDate() - i); d.setHours(0,0,0,0);
        list.push({ label: d.toLocaleDateString('en-US', { weekday: 'short' }), keyStart: d.getTime(), rides: 0 });
      }
    } else if (period === '1m') {
      // week buckets for current month
      const start = new Date(now.getFullYear(), now.getMonth(), 1); start.setHours(0,0,0,0);
      for (let w = 1; w <= 5; w++) {
        const wk = new Date(start); wk.setDate(start.getDate() + (w-1)*7);
        list.push({ label: `Week ${w}`, keyStart: wk.getTime(), rides: 0 });
      }
    } else if (period === '3m') {
      // last 3 months
      for (let i = 2; i >= 0; i--) {
        const m = new Date(now.getFullYear(), now.getMonth() - i, 1); m.setHours(0,0,0,0);
        list.push({ label: m.toLocaleString('en-US', { month: 'short' }), keyStart: m.getTime(), rides: 0 });
      }
    } else {
      // quarters in current year
      const quarters = [0,3,6,9];
      for (let qi = 0; qi < quarters.length; qi++) {
        const q = new Date(now.getFullYear(), quarters[qi], 1); q.setHours(0,0,0,0);
        list.push({ label: `Q${qi+1}`, keyStart: q.getTime(), rides: 0 });
      }
    }
    return list;
  }

  private bucketStart(d: Date, period: '7d' | '1m' | '3m' | '1y'): Date {
    if (period === '7d') { const x = new Date(d); x.setHours(0,0,0,0); return x; }
    if (period === '1m') {
      // approximate to week-of-month starting from day 1
      const start = new Date(d.getFullYear(), d.getMonth(), 1); start.setHours(0,0,0,0);
      const diffDays = Math.floor((d.getTime() - start.getTime()) / (1000*60*60*24));
      const weekIndex = Math.floor(diffDays / 7);
      const wk = new Date(start); wk.setDate(start.getDate() + weekIndex*7);
      return wk;
    }
    if (period === '3m') { const x = new Date(d.getFullYear(), d.getMonth(), 1); x.setHours(0,0,0,0); return x; }
    // '1y' -> quarter start
    const qStartMonth = Math.floor(d.getMonth()/3) * 3;
    const x = new Date(d.getFullYear(), qStartMonth, 1); x.setHours(0,0,0,0); return x;
  }

  private scaleValue(rides: number, buckets: {keyStart:number}[], byBucket: Map<number, number>): number {
    const max = Math.max(1, ...buckets.map(b => byBucket.get(b.keyStart) || 0));
    return Math.round((rides / max) * 100);
  }

  // Light realtime touch-up (optional)
  private refreshStatisticsRealtime(): void {
    if (!this.allTrips.length) return;
    // No mutation of backend data; could refetch instead
    this.recomputeAll();
  }

  // Navigation
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  // Formatters (reuse existing)
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency', currency: 'INR', minimumFractionDigits: 0, maximumFractionDigits: 0
    }).format(amount);
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

// Interfaces
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

interface ChartPeriod {
  label: string;
  value: string;
}

interface ChartData {
  label: string;
  value: number;
  rides: number;
}

interface RideStatus {
  name: string;
  count: number;
  percentage: number;
  color: string;
}

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

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Component state
  isLoading = false;
  selectedTimeFrame = 'today';
  selectedChartPeriod = '7d';

  // Chart periods
  chartPeriods: ChartPeriod[] = [
    { label: '7D', value: '7d' },
    { label: '1M', value: '1m' },
    { label: '3M', value: '3m' },
    { label: '1Y', value: '1y' }
  ];

  // Statistics data
  statistics: Statistics = {
    totalRides: 1247,
    activeDrivers: 89,
    totalRevenue: 245670,
    avgRating: 4.6,
    ridesChange: 12.5,
    driversChange: 8.3,
    revenueChange: 15.2,
    ratingChange: 94.5
  };

  // Chart data
  chartData: ChartData[] = [
    { label: 'Mon', value: 85, rides: 134 },
    { label: 'Tue', value: 92, rides: 147 },
    { label: 'Wed', value: 78, rides: 123 },
    { label: 'Thu', value: 95, rides: 152 },
    { label: 'Fri', value: 88, rides: 140 },
    { label: 'Sat', value: 100, rides: 160 },
    { label: 'Sun', value: 75, rides: 120 }
  ];

  // Ride status distribution
  rideStatusData: RideStatus[] = [
    { name: 'Completed', count: 856, percentage: 68.7, color: '#10b981' },
    { name: 'Ongoing', count: 234, percentage: 18.8, color: '#3b82f6' },
    { name: 'Pending', count: 98, percentage: 7.9, color: '#f59e0b' },
    { name: 'Cancelled', count: 59, percentage: 4.7, color: '#ef4444' }
  ];

  // Recent rides data
  recentRides: RecentRide[] = [
    {
      id: 'RID001',
      customerName: 'Rahul Sharma',
      pickup: 'Andheri West',
      destination: 'Bandra Kurla Complex',
      startTime: new Date(Date.now() - 1800000), // 30 minutes ago
      driverName: 'Suresh Kumar',
      status: 'completed',
      fare: 285
    },
    {
      id: 'RID002',
      customerName: 'Priya Patel',
      pickup: 'Powai',
      destination: 'Mumbai Airport T2',
      startTime: new Date(Date.now() - 3600000), // 1 hour ago
      driverName: 'Rajesh Singh',
      status: 'ongoing',
      fare: 450
    },
    {
      id: 'RID003',
      customerName: 'Amit Verma',
      pickup: 'Thane Station',
      destination: 'Lower Parel',
      startTime: new Date(Date.now() - 5400000), // 1.5 hours ago
      driverName: 'Vikram Yadav',
      status: 'completed',
      fare: 320
    },
    {
      id: 'RID004',
      customerName: 'Neha Gupta',
      pickup: 'Juhu Beach',
      destination: 'Worli Sea Link',
      startTime: new Date(Date.now() - 7200000), // 2 hours ago
      driverName: 'Manoj Tiwari',
      status: 'pending',
      fare: 180
    },
    {
      id: 'RID005',
      customerName: 'Karan Mehta',
      pickup: 'Ghatkopar East',
      destination: 'Nariman Point',
      startTime: new Date(Date.now() - 9000000), // 2.5 hours ago
      driverName: 'Deepak Joshi',
      status: 'cancelled',
      fare: 0
    }
  ];

  // Top drivers data
  topDrivers: TopDriver[] = [
    {
      id: 'DRV001',
      name: 'Suresh Kumar',
      vehicleNumber: 'MH 12 AB 1234',
      rating: 4.9,
      ridesCompleted: 156,
      earnings: 12450,
      isOnline: true
    },
    {
      id: 'DRV002',
      name: 'Rajesh Singh',
      vehicleNumber: 'MH 14 CD 5678',
      rating: 4.8,
      ridesCompleted: 142,
      earnings: 11280,
      isOnline: true
    },
    {
      id: 'DRV003',
      name: 'Vikram Yadav',
      vehicleNumber: 'MH 16 EF 9012',
      rating: 4.7,
      ridesCompleted: 138,
      earnings: 10950,
      isOnline: false
    },
    {
      id: 'DRV004',
      name: 'Manoj Tiwari',
      vehicleNumber: 'MH 18 GH 3456',
      rating: 4.6,
      ridesCompleted: 134,
      earnings: 10670,
      isOnline: true
    },
    {
      id: 'DRV005',
      name: 'Deepak Joshi',
      vehicleNumber: 'MH 20 IJ 7890',
      rating: 4.5,
      ridesCompleted: 128,
      earnings: 10240,
      isOnline: true
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.startDataRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Load dashboard data
  loadDashboardData(): void {
    this.isLoading = true;
    
    // Simulate API call
    setTimeout(() => {
      this.updateStatisticsBasedOnTimeFrame();
      this.updateChartData();
      this.isLoading = false;
    }, 1000);
  }

  // Refresh data
  refreshData(): void {
    this.loadDashboardData();
  }

  // Start periodic data refresh
  startDataRefresh(): void {
    // Refresh data every 5 minutes
    setInterval(() => {
      this.refreshStatistics();
    }, 300000);
  }

  // Time frame change handler
  onTimeFrameChange(): void {
    this.loadDashboardData();
  }

  // Set chart period
  setChartPeriod(period: string): void {
    this.selectedChartPeriod = period;
    this.updateChartData();
  }

  // Update statistics based on time frame
  updateStatisticsBasedOnTimeFrame(): void {
    const multipliers = {
      'today': { rides: 1, revenue: 1 },
      'week': { rides: 7, revenue: 7 },
      'month': { rides: 30, revenue: 30 },
      'year': { rides: 365, revenue: 365 }
    };

    const multiplier = multipliers[this.selectedTimeFrame as keyof typeof multipliers] || multipliers['today'];
    
    this.statistics = {
      ...this.statistics,
      totalRides: Math.floor(this.statistics.totalRides * multiplier.rides * (0.8 + Math.random() * 0.4)),
      totalRevenue: Math.floor(this.statistics.totalRevenue * multiplier.revenue * (0.8 + Math.random() * 0.4)),
      ridesChange: Number((Math.random() * 30 - 5).toFixed(1)),
      revenueChange: Number((Math.random() * 25 - 2).toFixed(1))
    };
  }

  // Update chart data based on selected period
  updateChartData(): void {
    const periods = {
      '7d': [
        { label: 'Mon', value: 85, rides: 134 },
        { label: 'Tue', value: 92, rides: 147 },
        { label: 'Wed', value: 78, rides: 123 },
        { label: 'Thu', value: 95, rides: 152 },
        { label: 'Fri', value: 88, rides: 140 },
        { label: 'Sat', value: 100, rides: 160 },
        { label: 'Sun', value: 75, rides: 120 }
      ],
      '1m': [
        { label: 'Week 1', value: 85, rides: 890 },
        { label: 'Week 2', value: 92, rides: 945 },
        { label: 'Week 3', value: 78, rides: 823 },
        { label: 'Week 4', value: 95, rides: 1012 }
      ],
      '3m': [
        { label: 'Jan', value: 85, rides: 3420 },
        { label: 'Feb', value: 92, rides: 3680 },
        { label: 'Mar', value: 78, rides: 3125 }
      ],
      '1y': [
        { label: 'Q1', value: 85, rides: 10890 },
        { label: 'Q2', value: 92, rides: 11450 },
        { label: 'Q3', value: 78, rides: 9823 },
        { label: 'Q4', value: 95, rides: 12012 }
      ]
    };

    this.chartData = periods[this.selectedChartPeriod as keyof typeof periods] || periods['7d'];
  }

  // Refresh statistics with real-time data
  refreshStatistics(): void {
    // Simulate real-time updates
    this.statistics = {
      ...this.statistics,
      totalRides: this.statistics.totalRides + Math.floor(Math.random() * 5),
      activeDrivers: Math.max(70, this.statistics.activeDrivers + Math.floor(Math.random() * 6 - 3)),
      totalRevenue: this.statistics.totalRevenue + Math.floor(Math.random() * 1000)
    };

    // Update ride status data
    this.updateRideStatusData();
    
    // Add new recent rides occasionally
    if (Math.random() < 0.3) {
      this.addNewRecentRide();
    }
  }

  // Update ride status distribution
  updateRideStatusData(): void {
    const total = this.statistics.totalRides;
    this.rideStatusData = [
      { name: 'Completed', count: Math.floor(total * 0.687), percentage: 68.7, color: '#10b981' },
      { name: 'Ongoing', count: Math.floor(total * 0.188), percentage: 18.8, color: '#3b82f6' },
      { name: 'Pending', count: Math.floor(total * 0.079), percentage: 7.9, color: '#f59e0b' },
      { name: 'Cancelled', count: Math.floor(total * 0.047), percentage: 4.6, color: '#ef4444' }
    ];
  }

  // Add new recent ride
  addNewRecentRide(): void {
    const customers = ['Arjun Shah', 'Sneha Reddy', 'Rohit Agarwal', 'Kavya Nair', 'Aditya Jain'];
    const drivers = ['Ramesh Patil', 'Sandeep Kaur', 'Naveen Kumar', 'Pooja Desai', 'Ashish Pandey'];
    const locations = [
      { pickup: 'Malad West', destination: 'Goregaon East' },
      { pickup: 'Kandivali', destination: 'Borivali Station' },
      { pickup: 'Santacruz', destination: 'Ville Parle' },
      { pickup: 'Jogeshwari', destination: 'Andheri Metro' }
    ];

    const location = locations[Math.floor(Math.random() * locations.length)];
    const newRide: RecentRide = {
      id: 'RID' + Date.now().toString().slice(-3),
      customerName: customers[Math.floor(Math.random() * customers.length)],
      pickup: location.pickup,
      destination: location.destination,
      startTime: new Date(),
      driverName: drivers[Math.floor(Math.random() * drivers.length)],
      status: 'ongoing',
      fare: Math.floor(Math.random() * 400) + 100
    };

    this.recentRides.unshift(newRide);
    if (this.recentRides.length > 5) {
      this.recentRides.pop();
    }
  }

  // Navigation methods
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  // Utility methods
  getTotalActiveRides(): number {
    return this.recentRides.filter(ride => ride.status === 'ongoing').length;
  }

  getTotalOnlineDrivers(): number {
    return this.topDrivers.filter(driver => driver.isOnline).length;
  }

  getAverageRideValue(): number {
    const completedRides = this.recentRides.filter(ride => ride.status === 'completed');
    if (completedRides.length === 0) return 0;
    
    const total = completedRides.reduce((sum, ride) => sum + ride.fare, 0);
    return Math.round(total / completedRides.length);
  }

  // Export data methods
  exportRidesData(): void {
    // Implementation for exporting rides data
    console.log('Exporting rides data...');
  }

  exportDriversData(): void {
    // Implementation for exporting drivers data
    console.log('Exporting drivers data...');
  }

  exportRevenueData(): void {
    // Implementation for exporting revenue data
    console.log('Exporting revenue data...');
  }

  // Filter methods
  filterRidesByStatus(status: string): RecentRide[] {
    return this.recentRides.filter(ride => ride.status === status);
  }

  filterDriversByStatus(isOnline: boolean): TopDriver[] {
    return this.topDrivers.filter(driver => driver.isOnline === isOnline);
  }

  // Format methods
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }

  formatPercentage(value: number): string {
    return `${value > 0 ? '+' : ''}${value.toFixed(1)}%`;
  }

  formatNumber(value: number): string {
    return new Intl.NumberFormat('en-IN').format(value);
  }

  // Error handling
  handleError(error: any): void {
    console.error('Dashboard error:', error);
    // Implement error handling logic
  }
}
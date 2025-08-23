// overview.component.ts
import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-overview',
  standalone: false, 

  template: `
    <div class="p-6 space-y-6 bg-gray-50 min-h-screen">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div *ngFor="let stat of stats; trackBy: trackByStatTitle" 
             class="bg-white rounded-xl shadow-sm p-6 flex items-start hover:shadow-md transition-all duration-300 transform hover:-translate-y-1">
          <div class="p-3 rounded-lg mr-4" [ngClass]="stat.color">
            <div class="w-6 h-6 flex items-center justify-center text-lg">
              {{stat.iconText}}
            </div>
          </div>
          <div>
            <p class="text-gray-500 text-sm">{{stat.title}}</p>
            <h3 class="text-2xl font-bold text-gray-900">{{stat.value}}</h3>
            <p class="text-green-600 text-sm font-medium flex items-center">
              <span class="inline-block w-2 h-2 bg-green-400 rounded-full mr-1"></span>
              {{stat.change}} from last month
            </p>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        
        <!-- Enhanced Bookings Bar Chart with Proper Axes -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-semibold text-gray-900">Monthly Bookings</h3>
            <button 
              (click)="refreshBookingData()"
              class="px-3 py-2 text-xs bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors duration-200 font-medium">
              Refresh Data
            </button>
          </div>
          
          <!-- Chart Container with Proper Axis Layout -->
          <div class="relative" style="height: 350px;">
            <!-- Chart Area -->
            <div class="absolute inset-0">
              <!-- Y-Axis -->
              <div class="absolute left-0 top-6 bottom-12 w-12 flex flex-col justify-between items-end pr-2">
                <span class="text-xs text-gray-600 font-medium">{{maxBookingValue}}</span>
                <span class="text-xs text-gray-600 font-medium">{{(maxBookingValue * 0.75) | number:'1.0-0'}}</span>
                <span class="text-xs text-gray-600 font-medium">{{(maxBookingValue * 0.5) | number:'1.0-0'}}</span>
                <span class="text-xs text-gray-600 font-medium">{{(maxBookingValue * 0.25) | number:'1.0-0'}}</span>
                <span class="text-xs text-gray-600 font-medium">0</span>
              </div>
              
              <!-- Chart Plot Area -->
              <div class="absolute left-12 right-4 top-6 bottom-12 border-l-2 border-b-2 border-gray-300">
                <!-- Horizontal Grid Lines -->
                <div class="absolute inset-0">
                  <div class="absolute top-0 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-1/4 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-1/2 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-3/4 left-0 right-0 border-t border-gray-200"></div>
                </div>
                
                <!-- Bars Container -->
                <div class="absolute inset-0 flex items-end">
                  <div *ngFor="let item of bookingData; let i = index; trackBy: trackByIndex" 
                       class="flex-1 flex flex-col items-center px-1">
                    
                    <!-- Bar -->
                    <div class="w-full max-w-12 relative group">
                      <div 
                        class="w-full bg-gradient-to-t from-blue-600 to-blue-400 rounded-t-md transition-all duration-700 ease-out hover:from-blue-700 hover:to-blue-500 shadow-sm hover:shadow-md cursor-pointer"
                        [style.height.px]="getBarHeight(item.value)"
                        [title]="item.label + ': ' + item.value + ' bookings'">
                        
                        <!-- Value Label on Hover -->
                        <div class="absolute -top-8 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-gray-800 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
                          {{item.value}}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- X-Axis Labels -->
              <div class="absolute left-12 right-4 bottom-0 h-12 flex items-center">
                <div *ngFor="let item of bookingData; let i = index" 
                     class="flex-1 text-center text-xs text-gray-700 font-semibold">
                  {{item.label}}
                </div>
              </div>
            </div>
            
            <!-- Chart Summary -->
            <div class="absolute bottom-14 right-6 text-xs text-gray-500 bg-white/90 backdrop-blur-sm px-3 py-2 rounded-lg shadow-sm border">
              <div class="flex items-center space-x-3">
                <span class="font-medium">Peak: {{maxBookingValue}}</span>
                <span class="text-gray-300">•</span>
                <span>Avg: {{averageBookings}}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Enhanced Revenue Line Chart with Proper Axes -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-semibold text-gray-900">Monthly Revenue</h3>
            <button 
              (click)="refreshRevenueData()"
              class="px-3 py-2 text-xs bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors duration-200 font-medium">
              Refresh Data
            </button>
          </div>
          
          <!-- Revenue Chart Container with Proper Axis Layout -->
          <div class="relative" style="height: 350px;">
            <!-- Chart Area -->
            <div class="absolute inset-0">
              <!-- Y-Axis for Revenue -->
              <div class="absolute left-0 top-6 bottom-12 w-16 flex flex-col justify-between items-end pr-2">
                <span class="text-xs text-gray-600 font-medium">\${{(maxRevenueValue/1000) | number:'1.0-0'}}k</span>
                <span class="text-xs text-gray-600 font-medium">\${{(maxRevenueValue*0.75/1000) | number:'1.0-0'}}k</span>
                <span class="text-xs text-gray-600 font-medium">\${{(maxRevenueValue*0.5/1000) | number:'1.0-0'}}k</span>
                <span class="text-xs text-gray-600 font-medium">\${{(maxRevenueValue*0.25/1000) | number:'1.0-0'}}k</span>
                <span class="text-xs text-gray-600 font-medium">\$0</span>
              </div>
              
              <!-- Chart Plot Area -->
              <div class="absolute left-16 right-4 top-6 bottom-12 border-l-2 border-b-2 border-gray-300">
                <!-- Horizontal Grid Lines -->
                <div class="absolute inset-0">
                  <div class="absolute top-0 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-1/4 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-1/2 left-0 right-0 border-t border-gray-200"></div>
                  <div class="absolute top-3/4 left-0 right-0 border-t border-gray-200"></div>
                </div>
                
                <!-- Line Chart Container -->
                <div class="absolute inset-0">
                  <!-- SVG for Line -->
                  <svg class="absolute inset-0 w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <polyline
                      [attr.points]="getLinePoints()"
                      fill="none"
                      stroke="#10b981"
                      stroke-width="0.5"
                      class="drop-shadow-sm"
                    />
                    <!-- Area under the line -->
                    <polygon
                      [attr.points]="getAreaPoints()"
                      fill="url(#revenueGradient)"
                      opacity="0.3"
                    />
                    <!-- Gradient definition -->
                    <defs>
                      <linearGradient id="revenueGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" style="stop-color:#10b981;stop-opacity:0.8" />
                        <stop offset="100%" style="stop-color:#10b981;stop-opacity:0.1" />
                      </linearGradient>
                    </defs>
                  </svg>
                  
                  <!-- Data Points -->
                  <div class="absolute inset-0 flex items-end">
                    <div *ngFor="let item of revenueData; let i = index" 
                         class="flex-1 flex justify-center"
                         [style.margin-bottom.px]="getRevenuePointPosition(item.value) - 4">
                      <div class="w-3 h-3 bg-green-500 rounded-full border-2 border-white shadow-md hover:w-4 hover:h-4 transition-all cursor-pointer group relative">
                        <!-- Value Label on Hover -->
                        <div class="absolute -top-10 left-1/2 transform -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-gray-800 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
                          \${{(item.value/1000) | number:'1.1-1'}}k
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- X-Axis Labels -->
              <div class="absolute left-16 right-4 bottom-0 h-12 flex items-center">
                <div *ngFor="let item of revenueData; let i = index" 
                     class="flex-1 text-center text-xs text-gray-700 font-semibold">
                  {{item.label}}
                </div>
              </div>
            </div>
            
            <!-- Revenue Chart Summary -->
            <div class="absolute bottom-14 right-6 text-xs text-gray-500 bg-white/90 backdrop-blur-sm px-3 py-2 rounded-lg shadow-sm border">
              <div class="flex items-center space-x-3">
                <span class="font-medium">Peak: \${{(maxRevenueValue/1000) | number:'1.0-0'}}k</span>
                <span class="text-gray-300">•</span>
                <span>Avg: \${{(averageRevenue/1000) | number:'1.0-0'}}k</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Bookings Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-900">Recent Bookings</h3>
            <span class="text-sm text-gray-500">{{recentBookings.length}} entries</span>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Booking ID</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Driver</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Pickup Location</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr *ngFor="let booking of recentBookings; trackBy: trackByBookingId" 
                  class="hover:bg-gray-50 transition-colors duration-150">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">{{booking.id}}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">{{booking.customer}}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{booking.driver}}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{booking.pickup}}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span [ngClass]="{
                    'bg-green-100 text-green-800 border-green-200': booking.status === 'Completed',
                    'bg-blue-100 text-blue-800 border-blue-200': booking.status === 'In Progress',
                    'bg-red-100 text-red-800 border-red-200': booking.status === 'Cancelled'
                  }" class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full border">
                    {{booking.status}}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-bold">{{booking.amount}}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="px-6 py-4 border-t border-gray-200 bg-gray-50">
          <button class="text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors duration-200 hover:underline">
            View all bookings →
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* Enhanced animations */
    @keyframes barGrow {
      from {
        height: 0px;
        opacity: 0;
      }
      to {
        opacity: 1;
      }
    }
    
    .bar-animation {
      animation: barGrow 0.8s ease-out forwards;
    }
    
    /* Custom scrollbar */
    .overflow-x-auto::-webkit-scrollbar {
      height: 6px;
    }
    
    .overflow-x-auto::-webkit-scrollbar-track {
      background: #f1f5f9;
      border-radius: 3px;
    }
    
    .overflow-x-auto::-webkit-scrollbar-thumb {
      background: #cbd5e1;
      border-radius: 3px;
    }
    
    .overflow-x-auto::-webkit-scrollbar-thumb:hover {
      background: #94a3b8;
    }
    
    /* Improved hover effects */
    .transform {
      transition: transform 0.2s ease-in-out;
    }
    
    /* Chart specific styles */
    .max-w-12 {
      max-width: 3rem;
    }
    
    /* Line chart animation */
    polyline {
      stroke-dasharray: 1000;
      stroke-dashoffset: 1000;
      animation: drawLine 2s ease-out forwards;
    }
    
    @keyframes drawLine {
      to {
        stroke-dashoffset: 0;
      }
    }
    
    /* Responsive adjustments */
    @media (max-width: 768px) {
      .stats-card {
        padding: 1rem;
      }
    }
  `]
})
export class OverviewComponent implements OnInit {

  // Enhanced booking data
  bookingData = [
    { label: 'Jan', value: 145 },
    { label: 'Feb', value: 198 },
    { label: 'Mar', value: 167 },
    { label: 'Apr', value: 234 },
    { label: 'May', value: 289 },
    { label: 'Jun', value: 276 },
    { label: 'Jul', value: 312 },
    { label: 'Aug', value: 298 }
  ];

  // Enhanced revenue data
  revenueData = [
    { label: 'Jan', value: 3200 },
    { label: 'Feb', value: 4100 },
    { label: 'Mar', value: 3800 },
    { label: 'Apr', value: 5200 },
    { label: 'May', value: 4800 },
    { label: 'Jun', value: 6100 },
    { label: 'Jul', value: 5900 },
    { label: 'Aug', value: 6300 }
  ];

  // Chart dimensions
  private readonly CHART_HEIGHT = 280;

  // Computed properties
  get maxBookingValue(): number {
    return Math.max(...this.bookingData.map(d => d.value));
  }

  get maxRevenueValue(): number {
    return Math.max(...this.revenueData.map(d => d.value));
  }

  get totalBookings(): number {
    return this.bookingData.reduce((sum, item) => sum + item.value, 0);
  }

  get totalRevenue(): number {
    return this.revenueData.reduce((sum, item) => sum + item.value, 0);
  }

  get averageBookings(): number {
    return Math.round(this.totalBookings / this.bookingData.length);
  }

  get averageRevenue(): number {
    return Math.round(this.totalRevenue / this.revenueData.length);
  }

  // Updated stats
  stats = [
    { 
      title: 'Total Bookings', 
      value: '2,119', 
      change: '+15%', 
      iconText: '📊',
      color: 'bg-blue-100 text-blue-600'
    },
    { 
      title: 'Active Cabs', 
      value: '73', 
      change: '+8%', 
      iconText: '🚕',
      color: 'bg-green-100 text-green-600'
    },
    { 
      title: 'Registered Drivers', 
      value: '142', 
      change: '+12%', 
      iconText: '👥',
      color: 'bg-purple-100 text-purple-600'
    },
    { 
      title: 'Total Revenue', 
      value: '$39,500', 
      change: '+22%', 
      iconText: '💰',
      color: 'bg-amber-100 text-amber-600'
    }
  ];

  // Sample booking data
  recentBookings = [
    { id: '#CB-1001', customer: 'John Doe', driver: 'Michael Smith', pickup: 'International Airport', status: 'Completed' as const, amount: '$45.50' },
    { id: '#CB-1002', customer: 'Sarah Johnson', driver: 'Robert Brown', pickup: 'Downtown Plaza', status: 'In Progress' as const, amount: '$32.75' },
    { id: '#CB-1003', customer: 'David Wilson', driver: 'James Davis', pickup: 'Grand Hotel', status: 'Cancelled' as const, amount: '$28.00' },
    { id: '#CB-1004', customer: 'Emily Taylor', driver: 'William Miller', pickup: 'Central Station', status: 'Completed' as const, amount: '$52.25' },
    { id: '#CB-1005', customer: 'Michael Anderson', driver: 'Daniel Thomas', pickup: 'Shopping Mall', status: 'Completed' as const, amount: '$38.80' },
    { id: '#CB-1006', customer: 'Lisa Chen', driver: 'Christopher Lee', pickup: 'Business District', status: 'In Progress' as const, amount: '$41.60' },
    { id: '#CB-1007', customer: 'Robert Garcia', driver: 'Matthew Johnson', pickup: 'University Campus', status: 'Completed' as const, amount: '$29.90' }
  ];

  ngOnInit() {
    console.log('Enhanced Overview component loaded');
    this.logChartData();
  }

  // Chart helper methods
  getBarHeight(value: number): number {
    const percentage = value / this.maxBookingValue;
    const maxHeight = this.CHART_HEIGHT - 40;
    return Math.max(percentage * maxHeight, 8);
  }

  getRevenuePointPosition(value: number): number {
    const percentage = value / this.maxRevenueValue;
    const maxHeight = this.CHART_HEIGHT - 40;
    return percentage * maxHeight;
  }

  // Generate SVG line points for revenue chart
  getLinePoints(): string {
    const points = this.revenueData.map((item, index) => {
      const x = (index / (this.revenueData.length - 1)) * 100;
      const y = 100 - (item.value / this.maxRevenueValue) * 100;
      return `${x},${y}`;
    });
    return points.join(' ');
  }

  // Generate area points for filled area under line
  getAreaPoints(): string {
    const linePoints = this.getLinePoints();
    const firstX = this.revenueData[0] ? '0' : '0';
    const lastX = this.revenueData.length > 0 ? '100' : '100';
    return `0,100 ${linePoints} ${lastX},100`;
  }

  // Data refresh methods
  refreshBookingData(): void {
    console.log('Refreshing booking data...');
    this.bookingData = this.bookingData.map(item => ({
      ...item,
      value: Math.floor(Math.random() * 200) + 100
    }));
    this.logChartData();
  }

  refreshRevenueData(): void {
    console.log('Refreshing revenue data...');
    this.revenueData = this.revenueData.map(item => ({
      ...item,
      value: Math.floor(Math.random() * 4000) + 3000
    }));
    this.logChartData();
  }

  // TrackBy functions for performance
  trackByIndex(index: number): number {
    return index;
  }

  trackByBookingId(index: number, booking: any): string {
    return booking.id;
  }

  trackByStatTitle(index: number, stat: any): string {
    return stat.title;
  }

  // Debug logging
  private logChartData(): void {
    console.log('Booking Data:', this.bookingData);
    console.log('Revenue Data:', this.revenueData);
    console.log('Max Booking Value:', this.maxBookingValue);
    console.log('Max Revenue Value:', this.maxRevenueValue);
  }
}
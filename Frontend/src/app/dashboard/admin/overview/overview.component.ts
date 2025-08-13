
// overview.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart';

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, ChartModule],
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {

  bookingData: any;
  revenueData: any;
  chartOptions: any;
  lineChartOptions: any;

  ngOnInit() {
    this.initializeCharts();
  }

  initializeCharts() {
    // Chart data for bookings (Bar Chart)
    this.bookingData = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
      datasets: [
        {
          label: 'Completed Bookings',
          backgroundColor: '#4CAF50',
          borderColor: '#4CAF50',
          data: [65, 59, 80, 81, 56, 55, 40]
        },
        {
          label: 'Cancelled Bookings',
          backgroundColor: '#F44336',
          borderColor: '#F44336',
          data: [28, 48, 40, 19, 86, 27, 90]
        }
      ]
    };

    // Revenue data for line chart
    this.revenueData = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
      datasets: [
        {
          label: 'Revenue ($)',
          data: [3200, 2800, 4100, 4500, 2900, 3600, 2200],
          fill: false,
          borderColor: '#2196F3',
          backgroundColor: 'rgba(33, 150, 243, 0.1)',
          tension: 0.4,
          pointBackgroundColor: '#2196F3',
          pointBorderColor: '#2196F3',
          pointHoverBackgroundColor: '#1976D2',
          pointHoverBorderColor: '#1976D2'
        }
      ]
    };

    // Bar Chart options
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        },
        title: {
          display: false
        }
      },
      scales: {
        x: {
          display: true,
          grid: {
            display: false
          },
          title: {
            display: true,
            text: 'Month'
          }
        },
        y: {
          display: true,
          title: {
            display: true,
            text: 'Bookings'
          },
          beginAtZero: true
        }
      }
    };

    // Line Chart options
    this.lineChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        },
        title: {
          display: false
        }
      },
      scales: {
        x: {
          display: true,
          grid: {
            display: false
          },
          title: {
            display: true,
            text: 'Month'
          }
        },
        y: {
          display: true,
          title: {
            display: true,
            text: 'Revenue ($)'
          },
          beginAtZero: true
        }
      },
      elements: {
        point: {
          radius: 6,
          hoverRadius: 8
        }
      }
    };
  }

  // Stats cards data
  stats = [
    { 
      title: 'Total Bookings', 
      value: '1,254', 
      change: '+12%', 
      icon: 'book_online',
      color: 'bg-blue-100 text-blue-600'
    },
    { 
      title: 'Active Cabs', 
      value: '48', 
      change: '+5%', 
      icon: 'directions_car',
      color: 'bg-green-100 text-green-600'
    },
    { 
      title: 'Registered Drivers', 
      value: '86', 
      change: '+8%', 
      icon: 'people',
      color: 'bg-purple-100 text-purple-600'
    },
    { 
      title: 'Total Revenue', 
      value: '$24,580', 
      change: '+18%', 
      icon: 'attach_money',
      color: 'bg-amber-100 text-amber-600'
    }
  ];

  // Recent bookings data
  recentBookings = [
    { id: '#CB-1001', customer: 'John Doe', driver: 'Michael Smith', pickup: 'Airport', status: 'Completed', amount: '$45' },
    { id: '#CB-1002', customer: 'Sarah Johnson', driver: 'Robert Brown', pickup: 'Downtown', status: 'In Progress', amount: '$32' },
    { id: '#CB-1003', customer: 'David Wilson', driver: 'James Davis', pickup: 'Hotel Grand', status: 'Cancelled', amount: '$28' },
    { id: '#CB-1004', customer: 'Emily Taylor', driver: 'William Miller', pickup: 'Central Station', status: 'Completed', amount: '$50' },
    { id: '#CB-1005', customer: 'Michael Anderson', driver: 'Daniel Thomas', pickup: 'Shopping Mall', status: 'Completed', amount: '$38' }
  ];
}

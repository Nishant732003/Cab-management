import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-driver',
  standalone: false, 

  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.css']
})
export class DriverComponent implements OnInit {
  drivers = [
    {
      id: 'DR-1001',
      name: 'Michael Smith',
      phone: '+1 (555) 123-4567',
      email: 'michael.smith@example.com',
      vehicle: 'Toyota Camry (ABC-1234)',
      rating: 4.8,
      status: 'active',
      trips: 142,
      joinedDate: '2022-03-15'
    },
    {
      id: 'DR-1002',
      name: 'Robert Brown',
      phone: '+1 (555) 234-5678',
      email: 'robert.brown@example.com',
      vehicle: 'Honda Accord (XYZ-5678)',
      rating: 4.6,
      status: 'active',
      trips: 118,
      joinedDate: '2022-05-22'
    },
    {
      id: 'DR-1003',
      name: 'James Davis',
      phone: '+1 (555) 345-6789',
      email: 'james.davis@example.com',
      vehicle: 'Ford Fusion (DEF-9012)',
      rating: 4.9,
      status: 'active',
      trips: 176,
      joinedDate: '2022-01-10'
    },
    {
      id: 'DR-1004',
      name: 'William Miller',
      phone: '+1 (555) 456-7890',
      email: 'william.miller@example.com',
      vehicle: 'Chevrolet Malibu (GHI-3456)',
      rating: 4.5,
      status: 'inactive',
      trips: 95,
      joinedDate: '2022-07-18'
    },
    {
      id: 'DR-1005',
      name: 'Daniel Thomas',
      phone: '+1 (555) 567-8901',
      email: 'daniel.thomas@example.com',
      vehicle: 'Nissan Altima (JKL-7890)',
      rating: 4.7,
      status: 'active',
      trips: 132,
      joinedDate: '2022-04-05'
    },
    {
      id: 'DR-1006',
      name: 'Christopher Lee',
      phone: '+1 (555) 678-9012',
      email: 'christopher.lee@example.com',
      vehicle: 'Hyundai Sonata (MNO-1234)',
      rating: 4.4,
      status: 'active',
      trips: 87,
      joinedDate: '2022-09-30'
    },
    {
      id: 'DR-1007',
      name: 'Matthew Johnson',
      phone: '+1 (555) 789-0123',
      email: 'matthew.johnson@example.com',
      vehicle: 'Kia Optima (PQR-5678)',
      rating: 4.9,
      status: 'active',
      trips: 154,
      joinedDate: '2022-02-14'
    }
  ];

  // Computed properties
  activeDriversCount: number = 0;
  averageRating: number = 0;
  totalTrips: number = 0;
  Math = Math; // Make Math available in template

  ngOnInit(): void {
    this.calculateStats();
  }

  calculateStats(): void {
    this.activeDriversCount = this.drivers.filter(d => d.status === 'active').length;
    this.averageRating = this.drivers.reduce((sum, driver) => sum + driver.rating, 0) / this.drivers.length;
    this.totalTrips = this.drivers.reduce((sum, driver) => sum + driver.trips, 0);
  }

  getStatusClass(status: string): string {
    return status === 'active' 
      ? 'bg-green-100 text-green-800 border-green-200' 
      : 'bg-gray-100 text-gray-800 border-gray-200';
  }

  trackByDriverId(index: number, driver: any): string {
    return driver.id;
  }
}
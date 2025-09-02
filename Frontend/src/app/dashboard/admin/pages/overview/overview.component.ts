import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // FIX: Import CommonModule

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule], // FIX: Add CommonModule here
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {
  // All your component's existing logic and properties remain here
  stats = [];
  bookingData = [];
  revenueData = [];
  topLocations = [];
  maxBookingValue = 0;
  maxRevenueValue = 0;
  averageRevenue = 0;
  
  constructor() { }

  ngOnInit(): void {
    // All your ngOnInit logic remains here
  }
}
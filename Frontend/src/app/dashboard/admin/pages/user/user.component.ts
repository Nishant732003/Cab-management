import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, Customer } from '../../../../core/services/admin.service'; // Import the service and interface
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule], // Ensure CommonModule is imported for *ngFor, etc.
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  
  customers: Customer[] = []; // Array to hold the list of customers
  isLoading = true; // Flag to show a loading indicator
  errorMessage = ''; // To store any potential error messages

  // Inject the new AdminService
  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.getAllCustomers().pipe(
      finalize(() => {
        this.isLoading = false; // Stop loading spinner when done
      })
    ).subscribe({
      next: (data) => {
        this.customers = data; // Assign fetched data to our customers array
      },
      error: (error) => {
        // Handle potential errors, e.g., if the user is not authorized
        this.errorMessage = 'Failed to load customer data. Please try again later.';
        console.error('Error fetching customers:', error);
      }
    });
  }

  // Optional: A method to refresh data
  refreshData(): void {
    this.loadCustomers();
  }
}
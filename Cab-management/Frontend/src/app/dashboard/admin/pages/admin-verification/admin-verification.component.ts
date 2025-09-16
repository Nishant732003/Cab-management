import { Component, OnInit } from '@angular/core';
import { Admin, AdminService } from '../../../../core/services/admin.service';
import { CommonModule } from '@angular/common'; // <-- IMPORT THIS

@Component({
  selector: 'app-admin-verification',
  standalone: true, // <-- ADD THIS
  imports: [CommonModule], // <-- AND THIS
  templateUrl: './admin-verification.component.html',
  styleUrls: ['./admin-verification.component.css']
})
export class AdminVerificationComponent implements OnInit {
  // --- Array to hold the list of unverified admins ---
  unverifiedAdmins: Admin[] = [];
  message: string = '';
  errorMessage: string = '';
  // --- ADDED: Loading state for better UX ---
  isLoading: boolean = true;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadUnverifiedAdmins();
  }

  loadUnverifiedAdmins(): void {
    // --- Set loading to true before fetching data ---
    this.isLoading = true;
    this.message = ''; // Clear previous messages
    this.errorMessage = '';
    this.adminService.getUnverifiedAdmins().subscribe({
      next: (data) => {
        this.unverifiedAdmins = data;
        this.isLoading = false; // --- Set loading to false after data is fetched ---
      },
      error: (err) => {
        this.errorMessage = 'Failed to load unverified admins.';
        console.error(err);
        this.isLoading = false; // --- Ensure loading is false even on error ---
      }
    });
  }

  verifyAdmin(adminId: number): void {
    this.adminService.verifyAdmin(adminId).subscribe({
      next: () => {
        this.message = `Admin with ID ${adminId} verified successfully.`;
        this.loadUnverifiedAdmins(); // Refresh the list
      },
      error: (err) => {
        this.errorMessage = `Failed to verify admin with ID ${adminId}.`;
        console.error(err);
      }
    });
  }
}
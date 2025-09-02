import { Component, OnInit } from '@angular/core';
import { Admin, AdminService } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-admin-verification',
  templateUrl: './admin-verification.component.html',
  styleUrls: ['./admin-verification.component.css']
})
export class AdminVerificationComponent implements OnInit {
  unverifiedAdmins: Admin[] = [];
  message: string = '';
  errorMessage: string = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadUnverifiedAdmins();
  }

  loadUnverifiedAdmins(): void {
    this.adminService.getUnverifiedAdmins().subscribe({
      next: (data) => {
        this.unverifiedAdmins = data;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load unverified admins.';
        console.error(err);
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
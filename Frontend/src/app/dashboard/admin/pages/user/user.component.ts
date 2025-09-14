import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, Customer } from '../../../../core/services/admin.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  paginatedCustomers: Customer[] = [];

  isLoading = true;
  errorMessage = '';
  showDeleteModal = false;
  customerToDelete: Customer | null = null;

  searchTerm = '';
  currentPage = 1;
  itemsPerPage = 6;

  private colorPalette: string[] = [
    '#1abc9c', '#2ecc71', '#3498db', '#9b59b6', '#e67e22',
    '#e74c3c', '#16a085', '#27ae60', '#2980b9', '#8e44ad',
    '#f39c12', '#d35400', '#c0392b', '#7f8c8d', '#2c3e50'
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  private recomputeFiltered(): void {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      this.filteredCustomers = this.customers.slice();
    } else {
      this.filteredCustomers = this.customers.filter(c =>
        (c.firstName ?? '').toLowerCase().includes(term) ||
        (c.lastName ?? '').toLowerCase().includes(term) ||
        (c.username ?? '').toLowerCase().includes(term) ||
        (c.email ?? '').toLowerCase().includes(term)
      );
    }
  }

  private recomputePagination(): void {
    const totalPages = this.totalPages;
    if (this.currentPage > totalPages && totalPages > 0) {
      this.currentPage = totalPages;
    }
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedCustomers = this.filteredCustomers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  private refreshView(): void {
    this.recomputeFiltered();
    this.recomputePagination();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getAllCustomers().pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
      next: (data) => {
        this.customers = data ?? [];
        this.refreshView();
      },
      error: (error) => {
        this.errorMessage = 'Failed to load customer data. Please try again later.';
        console.error('Error fetching customers:', error);
        this.customers = [];
        this.refreshView();
      }
    });
  }

  refreshData(): void {
    this.loadCustomers();
  }

  onSearchChange(value: string): void {
    this.searchTerm = value;
    this.currentPage = 1;
    this.refreshView();
  }

  changePage(page: number): void {
    const total = this.totalPages;
    if (page >= 1 && page <= total) {
      this.currentPage = page;
      this.recomputePagination();
    }
  }
  isDeleting = false;

  get totalPages(): number {
    return Math.ceil(this.filteredCustomers.length / this.itemsPerPage) || 0;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  getInitials(firstName?: any, lastName?: any, username?: any, email?: any): string {
    const safe = (v: any) => (v == null ? '' : String(v).trim());
    const f = safe(firstName);
    const l = safe(lastName);
    if (f || l) {
      const a = f ? f[0] : '';
      const b = l ? l[0] : '';
      return (a + b || f.substring(0, 2)).toUpperCase();
    }
    const u = safe(username) || safe(email);
    if (!u) return 'U';
    const parts = u.split(/[\s._-]+/).filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return u.substring(0, 2).toUpperCase();
  }

  private hashString(s: string): number {
    let hash = 0;
    for (let i = 0; i < s.length; i++) {
      hash = ((hash << 5) - hash) + s.charCodeAt(i);
      hash |= 0;
    }
    return Math.abs(hash);
  }

  getAvatarColor(customer: Customer): string {
    const key = `${customer.username ?? ''}|${customer.email ?? ''}|${customer.userId ?? ''}`;
    const h = this.hashString(key || 'default');
    return this.colorPalette[h % this.colorPalette.length];
  }
openDeleteModal(customer: Customer): void {
  this.customerToDelete = customer; 
  this.showDeleteModal = true;
}


  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.customerToDelete = null;
  }

 
confirmDelete(): void {
  if (!this.customerToDelete?.username) {
    alert('Username is missing for the selected user.');
    return;
  }
  const username = this.customerToDelete.username; // username from the clicked row
  this.isDeleting = true;

  this.adminService.deleteUser(username).subscribe({
    next: () => {
      // Remove by username to mirror server identifier
      this.customers = this.customers.filter(c => c.username !== username);
      this.refreshView();
      const name =
        `${this.customerToDelete?.firstName ?? ''} ${this.customerToDelete?.lastName ?? ''}`.trim() ||
        username;
      this.isDeleting = false;
      this.closeDeleteModal();
      alert(`Customer ${name} has been deleted successfully.`);
    },
    error: (err) => {
      console.error('Delete failed:', err);
      this.isDeleting = false;
      alert('Failed to delete the user. Please try again.');
    }
  });
}

  exportToCSV(): void {
    const csvContent = this.generateCSV();
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'customers.csv';
    link.click();
    window.URL.revokeObjectURL(url);
  }

  private generateCSV(): string {
    const headers = ['Name', 'Username', 'Email', 'Mobile Number', 'User ID'];
    const csvRows = [headers.join(',')];
    this.customers.forEach(customer => {
      const row = [
        `${customer.firstName ?? ''} ${customer.lastName ?? ''}`.trim(),
        customer.username ?? '',
        customer.email ?? '',
        customer.mobileNumber ?? '',
        (customer.userId ?? '').toString()
      ];
      // Escape commas and quotes
      const escaped = row.map(v => `"${String(v).replace(/"/g, '""')}"`);
      csvRows.push(escaped.join(','));
    });
    return csvRows.join('\n');
  }
}

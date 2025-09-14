import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, Admin, Driver, Customer } from '../../../../core/services/admin.service';

interface StatCard {
  title: string;
  value: number | string;
  icon: string;
}

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {
  admin: Admin | null = null;
  adminImageUrl: string | null = null;
  
  stats: StatCard[] = [];
  drivers: Driver[] = [];
  customers: Customer[] = [];
  errorMsg: string | null = null;
  
  // Loading states
  isLoading = true;
  
  constructor(private adminSvc: AdminService) {}
  
  ngOnInit(): void {
    this.loadAdminAndStats();
  }
  
  private loadAdminAndStats(): void {
    this.errorMsg = null;
    this.isLoading = true;
    
    const userStr = localStorage.getItem('currentUser');
    if (!userStr) {
      this.errorMsg = 'User not authenticated';
      this.isLoading = false;
      return;
    }
    
    let username: string | null = null;
    try {
      const user = JSON.parse(userStr);
      username = user?.username ?? null;
    } catch {
      this.errorMsg = 'Failed to parse user data';
      this.isLoading = false;
      return;
    }
    
    if (!username) {
      this.errorMsg = 'Invalid user data';
      this.isLoading = false;
      return;
    }
    
    // Load admin profile
    this.adminSvc.getAdminProfile(username).subscribe({
      next: (profile) => {
        this.admin = profile;
        this.adminImageUrl = this.adminSvc.getProfileImageUrl(profile.profilePhotoUrl || null);
      },
      error: (error) => {
        console.error('Failed to load admin profile:', error);
        this.errorMsg = 'Failed to load admin profile';
      }
    });
    
    // Load drivers
    this.adminSvc.getAllDrivers().subscribe({
      next: (drivers) => {
        this.drivers = drivers;
        this.updateStats();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load drivers:', error);
        this.errorMsg = 'Failed to load drivers';
        this.isLoading = false;
      }
    });
    
    // Load customers
    this.adminSvc.getAllCustomers().subscribe({
      next: (customers) => {
        this.customers = customers;
        this.updateStats();
      },
      error: (error) => {
        console.error('Failed to load customers:', error);
        this.errorMsg = 'Failed to load customers';
      }
    });
  }
  
  get adminInitial(): string {
    if (!this.admin?.firstName && !this.admin?.lastName) {
      return 'AD';
    }
    
    const firstInitial = this.admin?.firstName?.charAt(0) || '';
    const lastInitial = this.admin?.lastName?.charAt(0) || '';
    
    return (firstInitial + lastInitial).toUpperCase() || 'AD';
  }
  
  private updateStats(): void {
    const totalDrivers = this.drivers.length;
    const verifiedDrivers = this.drivers.filter(d => d.verified === true).length;
    const totalCustomers = this.customers.length;
    
    this.stats = [
      { 
        title: 'Total Drivers', 
        value: totalDrivers, 
        icon: 'ri-steering-2-line' 
      },
      { 
        title: 'Verified Drivers', 
        value: verifiedDrivers, 
        icon: 'ri-verified-badge-line' 
      },
      { 
        title: 'Total Customers', 
        value: totalCustomers, 
        icon: 'ri-user-3-line' 
      }
    ];
  }
  
  // Add these methods to your component class:

getVerifiedDriversCount(): number {
  return this.drivers.filter(d => d.verified === true).length;
}


  // Additional computed values for enhanced stats
  getActiveRides(): number {
    // Simulate active rides based on verified drivers
    return Math.floor(this.drivers.filter(d => d.verified).length * 0.3);
  }
  
  getAverageRating(): string {
    if (this.drivers.length === 0) return '0.0';
    
    const totalRating = this.drivers.reduce((sum, driver) => sum + (driver.rating || 0), 0);
    const avgRating = totalRating / this.drivers.length;
    
    return avgRating.toFixed(1);
  }
  
  // Get top drivers sorted by rating
  getTopDrivers(): Driver[] {
    return this.drivers
      .filter(driver => driver.rating && driver.rating > 0)
      .sort((a, b) => (b.rating || 0) - (a.rating || 0))
      .slice(0, 5);
  }
  
  // Get recent customers (last 4)
  getRecentCustomers(): Customer[] {
    return this.customers.slice(-4).reverse();
  }
  
  // Get driver initials for avatar
  getDriverInitials(driver: Driver): string {
    const firstInitial = driver.firstName?.charAt(0) || '';
    const lastInitial = driver.lastName?.charAt(0) || '';
    return (firstInitial + lastInitial).toUpperCase() || 'DR';
  }
  
  // Get customer initials for avatar
  getCustomerInitials(customer: Customer): string {
    const firstInitial = customer.firstName?.charAt(0) || '';
    const lastInitial = customer.lastName?.charAt(0) || '';
    return (firstInitial + lastInitial).toUpperCase() || 'CU';
  }
  
  // Refresh data method
  refreshData(): void {
    this.loadAdminAndStats();
  }
  
  // Utility methods for template
  getVerificationStatus(verified: boolean | null | undefined): string {
    return verified ? 'Verified' : 'Unverified';
  }
  
  getVerifiedDriversPercentage(): number {
    if (this.drivers.length === 0) return 0;
    const verified = this.drivers.filter(d => d.verified === true).length;
    return Math.round((verified / this.drivers.length) * 100);
  }
  
  getUnverifiedDriversCount(): number {
    return this.drivers.filter(d => d.verified === false).length;
  }
  
  // Get recent activity summary
  getRecentActivitySummary(): string {
    const recentDrivers = this.drivers.slice(-3).length;
    const recentCustomers = this.customers.slice(-2).length;
    
    if (recentDrivers > 0 && recentCustomers > 0) {
      return `${recentDrivers} new drivers, ${recentCustomers} new customers`;
    } else if (recentDrivers > 0) {
      return `${recentDrivers} new drivers joined`;
    } else if (recentCustomers > 0) {
      return `${recentCustomers} new customers joined`;
    } else {
      return 'No recent activity';
    }
  }
  
  // Format mobile number for display
  formatMobileNumber(mobile: string | null | undefined): string {
    if (!mobile) return 'Not provided';
    
    if (mobile.length === 10) {
      return `${mobile.slice(0, 3)}-${mobile.slice(3, 6)}-${mobile.slice(6)}`;
    }
    
    return mobile;
  }
  
  // Get driver status color class
  getDriverStatusClass(driver: Driver): string {
    if (driver.verified) {
      return 'status-verified';
    } else {
      return 'status-pending';
    }
  }
  
  // Calculate total revenue (simulated)
  getTotalRevenue(): string {
    const baseRevenue = this.drivers.length * 1500; // Simulate revenue per driver
    const customerMultiplier = this.customers.length * 200;
    const total = baseRevenue + customerMultiplier;
    
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(total);
  }
  
  // Get system health status
  getSystemHealth(): 'excellent' | 'good' | 'fair' | 'poor' {
    const verifiedPercentage = this.getVerifiedDriversPercentage();
    
    if (verifiedPercentage >= 90) return 'excellent';
    if (verifiedPercentage >= 75) return 'good';
    if (verifiedPercentage >= 50) return 'fair';
    return 'poor';
  }
}
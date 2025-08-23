import { Component, OnInit } from '@angular/core';

interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  status: 'active' | 'inactive' | 'suspended';
  role: 'customer' | 'driver' | 'admin';
  joinDate: string;
  lastLogin: string;
  totalRides: number;
  totalSpent: number;
}

@Component({
  selector: 'app-users',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
  standalone: false
})
export class UsersComponent implements OnInit {
  // Expose Math object to template
  Math = Math;
  
  // Sample users data
  users: User[] = [
    {
      id: 'USR-1001',
      name: 'John Doe',
      email: 'john.doe@example.com',
      phone: '+1 (555) 123-4567',
      status: 'active',
      role: 'customer',
      joinDate: '2023-01-15',
      lastLogin: '2023-12-01',
      totalRides: 24,
      totalSpent: 856.50
    },
    {
      id: 'USR-1002',
      name: 'Sarah Johnson',
      email: 'sarah.j@example.com',
      phone: '+1 (555) 234-5678',
      status: 'active',
      role: 'customer',
      joinDate: '2023-02-20',
      lastLogin: '2023-12-01',
      totalRides: 18,
      totalSpent: 642.75
    },
    {
      id: 'USR-1003',
      name: 'Michael Brown',
      email: 'michael.b@example.com',
      phone: '+1 (555) 345-6789',
      status: 'inactive',
      role: 'customer',
      joinDate: '2023-03-10',
      lastLogin: '2023-11-15',
      totalRides: 8,
      totalSpent: 285.00
    },
    {
      id: 'USR-1004',
      name: 'Emily Davis',
      email: 'emily.d@example.com',
      phone: '+1 (555) 456-7890',
      status: 'active',
      role: 'customer',
      joinDate: '2023-04-05',
      lastLogin: '2023-12-01',
      totalRides: 32,
      totalSpent: 1124.80
    },
    {
      id: 'USR-1005',
      name: 'Robert Wilson',
      email: 'robert.w@example.com',
      phone: '+1 (555) 567-8901',
      status: 'suspended',
      role: 'customer',
      joinDate: '2023-05-12',
      lastLogin: '2023-10-20',
      totalRides: 12,
      totalSpent: 423.60
    },
    {
      id: 'USR-1006',
      name: 'Lisa Garcia',
      email: 'lisa.g@example.com',
      phone: '+1 (555) 678-9012',
      status: 'active',
      role: 'customer',
      joinDate: '2023-06-18',
      lastLogin: '2023-12-01',
      totalRides: 29,
      totalSpent: 987.25
    },
    {
      id: 'USR-1007',
      name: 'David Martinez',
      email: 'david.m@example.com',
      phone: '+1 (555) 789-0123',
      status: 'active',
      role: 'customer',
      joinDate: '2023-07-22',
      lastLogin: '2023-12-01',
      totalRides: 15,
      totalSpent: 532.90
    }
  ];

  // Filter and search properties
  searchQuery: string = '';
  statusFilter: string = 'all';
  roleFilter: string = 'all';
  sortBy: string = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;
  
  // Computed properties
  get filteredUsers(): User[] {
    let filtered = this.users.filter(user => {
      const matchesSearch = user.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                           user.email.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                           user.phone.includes(this.searchQuery);
      
      const matchesStatus = this.statusFilter === 'all' || user.status === this.statusFilter;
      const matchesRole = this.roleFilter === 'all' || user.role === this.roleFilter;
      
      return matchesSearch && matchesStatus && matchesRole;
    });

    // Sorting
    filtered.sort((a, b) => {
      let valueA: any, valueB: any;
      
      switch (this.sortBy) {
        case 'name': valueA = a.name; valueB = b.name; break;
        case 'email': valueA = a.email; valueB = b.email; break;
        case 'joinDate': valueA = new Date(a.joinDate); valueB = new Date(b.joinDate); break;
        case 'totalRides': valueA = a.totalRides; valueB = b.totalRides; break;
        case 'totalSpent': valueA = a.totalSpent; valueB = b.totalSpent; break;
        default: valueA = a.name; valueB = b.name;
      }
      
      if (valueA < valueB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valueA > valueB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    return filtered;
  }

  get paginatedUsers(): User[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  get totalUsers(): number {
    return this.users.length;
  }

  get activeUsers(): number {
    return this.users.filter(user => user.status === 'active').length;
  }

  get inactiveUsers(): number {
    return this.users.filter(user => user.status === 'inactive').length;
  }

  get suspendedUsers(): number {
    return this.users.filter(user => user.status === 'suspended').length;
  }

  ngOnInit(): void {
    console.log('Users Management component loaded');
  }

  // Sorting
  sort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDirection = 'asc';
    }
  }

  // Pagination
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // User actions
  editUser(user: User): void {
    console.log('Edit user:', user);
    // Implement edit logic
  }

  viewUserDetails(user: User): void {
    console.log('View user details:', user);
    // Implement view details logic
  }

  changeUserStatus(user: User, status: User['status']): void {
    console.log(`Change user ${user.id} status to:`, status);
    user.status = status;
    // Implement status change logic
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to delete ${user.name}?`)) {
      console.log('Delete user:', user);
      this.users = this.users.filter(u => u.id !== user.id);
    }
  }

  // Export functions
  exportToCSV(): void {
    console.log('Exporting users to CSV');
    // Implement CSV export logic
  }

  exportToExcel(): void {
    console.log('Exporting users to Excel');
    // Implement Excel export logic
  }

  // TrackBy function for performance
  trackByUserId(index: number, user: User): string {
    return user.id;
  }

  // Get status badge class
  getStatusClass(status: string): string {
    switch (status) {
      case 'active': return 'bg-green-100 text-green-800 border-green-200';
      case 'inactive': return 'bg-gray-100 text-gray-800 border-gray-200';
      case 'suspended': return 'bg-red-100 text-red-800 border-red-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  }

  // Get role badge class
  getRoleClass(role: string): string {
    switch (role) {
      case 'admin': return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'driver': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'customer': return 'bg-amber-100 text-amber-800 border-amber-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  }
}

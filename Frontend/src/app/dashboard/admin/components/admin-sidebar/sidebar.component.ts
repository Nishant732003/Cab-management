import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';

interface Route {
  label: string;
  icon: string;
  href: string;
  color: string;
  bgColor: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  currentPath: string = '';

  routes: Route[] = [
    {
      label: "Overview",
      icon: "LayoutDashboard",
      href: "/admin/overview",
      color: "text-pink-600",
      bgColor: "bg-pink-50",
    },
    {
      label: "Driver Management",
      icon: "Ambulance", 
      href: "/admin/drivers",
      color: "text-blue-600",
      bgColor: "bg-blue-50",
    },
    {
      label: "Customer Management",
      icon: "Users",
      href: "/admin/users",
      color: "text-green-600",
      bgColor: "bg-green-50",
    },
    {
      label: "Admin Verification",
      icon: "ShieldCheck", 
      href: "/admin/admin-verification",
      color: "text-yellow-600",
      bgColor: "bg-yellow-50",
    },
    {
      label: "Trip Management",
      icon: "Route", 
      href: "/admin/trips",
      color: "text-purple-600",
      bgColor: "bg-purple-50",
    },
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.currentPath = this.router.url;

    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => { // <-- CORRECTED THIS LINE
        this.currentPath = event.url;
      });
  }

  isActive(href: string): boolean {
    return this.currentPath.startsWith(href);
  }
}
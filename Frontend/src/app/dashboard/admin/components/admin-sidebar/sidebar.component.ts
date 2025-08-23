import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';

interface Route {
  label: string;
  icon: string;
  href: string;
  color: string;
  bgColor: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  currentPath: string = '';

  routes: Route[] = [
    {
      label: "Overview",
      icon: "LayoutDashboard",
      href: "/admins/overview",
      color: "text-pink-600",
      bgColor: "bg-pink-50",
    },
    {
      label: "Driver Management",
      icon: "Activity",
      href: "/admin/driver",
      color: "text-blue-600",
      bgColor: "bg-blue-50",
    },
    {
      label: "Customer Management",
      icon: "Users",
      href: "/admin/user",
      color: "text-green-600",
      bgColor: "bg-green-50",
    },
   
   
   
    {
      label: "Settings",
      icon: "Settings",
      href: "/admin/settings",
      color: "text-gray-600",
      bgColor: "bg-gray-50",
    },
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.currentPath = this.router.url;
    
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentPath = event.url;
      });
  }

  isActive(href: string): boolean {
    return this.currentPath.startsWith(href);
  }
}
import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';

interface Route {
  label: string;
  icon: string;
  href: string;
}

@Component({
  selector: 'user-sidebar',
  standalone: false, 
  templateUrl: './user-sidebar.component.html',
  styleUrls: ['./user-sidebar.component.css']
})
export class UserSidebarComponent implements OnInit {
  currentPath: string = '';

  // Routes for user interface
  routes: Route[] = [
    {
      label: "Dashboard",
      icon: "LayoutDashboard",
      href: "/user/overview"
    },
    {
      label: "Book Ride", 
      icon: "MapPin",
      href: "/user/book-ride"
    },
    {
      label: "My Trips",
      icon: "Activity",
      href: "/user/trip-history"
    },
  
    {
      label: "Settings",
      icon: "Settings",
      href: "/user/settings"
    }
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
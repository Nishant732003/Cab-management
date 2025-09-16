import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';

interface Route {
  label: string;
  icon: string;
  href: string;
}

@Component({
  selector: 'driver-sidebar',
  standalone: false, 
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  currentPath: string = '';

  // Routes that match the icons available in your template
  routes: Route[] = [
    {
      label: "Overview",
      icon: "LayoutDashboard",
      href: "/driver/overview"
    },
    {
      label: "My Trips", 
      icon: "Activity",
      href: "/driver/trips"
    },
    // {
    //   label: "Earnings",
    //   icon: "BadgeIndianRupee", 
    //   href: "/driver/earnings"
    // },
    {
      label: "My Vehicle",
      icon: "Ambulance",
      href: "/driver/vehicle"
    },
    // {
    //   label: "Profile",
    //   icon: "BookUser",
    //   href: "/driver/profile"
    // },
    // {
    //   label: "Settings",
    //   icon: "Settings",
    //   href: "/driver/settings"
    // }
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
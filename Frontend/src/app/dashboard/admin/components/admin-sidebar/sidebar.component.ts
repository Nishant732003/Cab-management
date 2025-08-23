import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from '../../../../../../node_modules/rxjs/dist/types/operators';

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
      href: "/admin/overview", // Fixed: was "/admins/overview"
      color: "text-pink-600",
      bgColor: "bg-pink-50",
    },
    {
      label: "Driver Management",
      icon: "Activity",
      href: "/admin/drivers", // Fixed: was "/admin/driver", now matches route
      color: "text-blue-600",
      bgColor: "bg-blue-50",
    },
    {
      label: "Customer Management",
      icon: "Users",
      href: "/admin/users", // You'll need to add this route to your routing module
      color: "text-green-600",
      bgColor: "bg-green-50",
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
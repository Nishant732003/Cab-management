import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DriverRoutingModule } from './driver-routing.module';

// Driver Components
import { DriverComponent } from './components/driver-dashboard/driver.component';
import { NavbarComponent } from './components/driver-navbar/navbar.component';
import { SidebarComponent } from './components/driver-sidebar/sidebar.component';

// Page Components
import { EarningsComponent } from './pages/earnings/earnings.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { TripsComponent } from './pages/trips/trip.component';

// Services
// import { TripService } from '../core/services/trip.service';
// import { EarningsService } from '../core/services/earnings.service';

@NgModule({
  declarations: [
    DriverComponent,
    NavbarComponent,
    SidebarComponent,
    EarningsComponent,
    OverviewComponent,
    TripsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    DriverRoutingModule
  ],
//   providers: [
//     TripService,
//     EarningsService
//   ]
})
export class DriverModule { }
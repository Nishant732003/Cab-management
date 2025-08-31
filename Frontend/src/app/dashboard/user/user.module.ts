import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserAuthContext } from '../../redux/context/UserAuthContext';

// Routing
import { UserRoutingModule } from './user-routing.module';
import { UsersCustomerComponent } from './components/user-dashboard/user.component';
// Main component


// Page components
import { BookRideComponent } from './pages/book-ride/bookRide.component';
import { CurrentRidesComponent } from './pages/current-ride/currentRide.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { TripHistoryComponent } from './pages/triphistory/trips.component';

// Dashboard components

import { UserNavbarComponent } from './components/user-navbar/user-navbar.component';
import { UserSidebarComponent } from './components/user-sidebar/user-sidebar.component';

// Services (if any user-specific services)
// import { UserService } from './services/user.service';
// import { RideService } from './services/ride.service';
// import { BookingService } from './services/booking.service';

@NgModule({
  declarations: [
    // Main component
    // UsersComponent,
  
    // Page components\
    UsersCustomerComponent,
    BookRideComponent,
    CurrentRidesComponent,
    OverviewComponent,
    TripHistoryComponent,
    
    // Dashboard components
  
    UserNavbarComponent,
    UserSidebarComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FormsModule,
    ReactiveFormsModule,
      RouterModule,
   
  ],
  providers: [
  UserAuthContext
  ]
})
export class UserModule { }